package com.hc.solr.service.internal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.hc.solr.entity.GoodsEntity;
import com.hc.solr.service.SolrService;

@Service("solrService")
public class SolrServiceImpl implements SolrService {
  private Logger logger = Logger.getLogger(SolrServiceImpl.class);

  @Autowired
  private HttpSolrClient solrClient;

  @Override
  public void addDocument(GoodsEntity goods) {
    Validate.notNull(goods, "商品不能为空");
    // 创建一个文档对象
    SolrInputDocument document = new SolrInputDocument();
    // 向文档中添加域
    // 第一个参数：域的名称，域的名称必须是在schema.xml中定义的
    // 第二个参数：域的值
    document.addField("id", goods.getId());
    document.addField("name", goods.getName());
    document.addField("desc", goods.getDesc());
    document.addField("category", goods.getCategory());
    document.addField("brand", goods.getBrand());
    document.addField("price", goods.getPrice().toString());
    // document.addField("imagePath", goods.getImagePath());
    try {
      // 把document对象添加到索引库中
      solrClient.add(document);
      // 提交修改
      solrClient.commit();
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }

  }

  @Override
  public void delDocumentById(String documentId) {
    Validate.notBlank(documentId, "documentId不能为空");
    try {
      solrClient.deleteById(documentId);
      // 提交修改
      solrClient.commit();
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }


  }

  @Override
  public void delDocumentByQuery(String query) {
    Validate.notBlank(query, "query不能为空");
    try {
      solrClient.deleteByQuery(query);
      // 提交修改
      solrClient.commit();
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
  }

  /**
   * 特殊字符的处理.
   * 
   * @param s 字符串
   * @return String
   */
  private String escapeQueryChars(String s) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      // 这些字符是查询语法的一部分，必须转义
      if (c == '\\' || c == '+' || c == '-' || c == '!' || c == '(' || c == ')' || c == ':'
          || c == '^' || c == '[' || c == ']' || c == '\"' || c == '{' || c == '}' || c == '~'
          || c == '*' || c == '?' || c == '|' || c == '&' || c == ';' || c == '/'
          || Character.isWhitespace(c)) {
        sb.append('\\');
      }
      sb.append(c);
    }
    return sb.toString();
  }

  /**
   * 一个字段 有多个关键字处理.
   * 
   * @param keyWord 搜索关键字
   * @param fileName 字段名
   * @return StringBuilder
   */
  private StringBuilder conSearchFiled(String keyWord, String fileName) {
    StringBuilder condition = null;
    if (StringUtils.isNotBlank(keyWord)) {
      condition = new StringBuilder();
      keyWord = keyWord.replaceAll(" ", "|").replaceAll(",", "|").replaceAll("，", "|");
      String[] words = null;
      if (keyWord.indexOf("|") != -1) {
        words = keyWord.split("\\|");
      }
      int index = 0;
      if (words != null) {
        for (String word : words) {
          if (index == 0) {
            condition.append("(");
          } else {
            condition.append(" OR ");
          }
          condition.append(fileName + ":" + word);
          index++;
          if (index == words.length) {
            condition.append(")");
          }
        }
      } else {
        condition.append(fileName + ":" + keyWord);
      }
    }
    return condition;
  }

  /**
   * 构造搜索条件（包含多个搜索字段）.
   * 
   * @param keyWord 关键字
   * @param map 其它搜索条件
   * @return String
   */
  private String getSearchCondtion(String keyWord, Map<String, Object> map) {
    // solr特殊字符处理
    if (StringUtils.isNotBlank(keyWord)) {
      keyWord = escapeQueryChars(keyWord);
    }
    // 一个字段，多个关键字处理
    StringBuilder condition = conSearchFiled(keyWord, "all");
    if (map != null) {
      for (String key : map.keySet()) {
        if (condition == null) {
          condition = new StringBuilder();
        } else {
          condition.append(" AND ");
        }
        condition.append(" " + key + ":" + map.get(key));
      }
    }
    if (condition == null) {
      return null;
    } else {
      return condition.toString();
    }
  }

  /**
   * 从查询到的solr中获取数据.
   * 
   * @param conditon 搜索条件
   * @param orderByFiled 排序字段
   * @param ascOrDesc 升序降序
   * @param list 返回的公司对象
   * @return SolrDocumentList
   */
  private void getListFromSolr(QueryResponse response, SolrDocumentList resultList,
      List<GoodsEntity> list) {
    Map<String, Map<String, List<String>>> resultHigh = response.getHighlighting();
    for (int i = 0; i < resultList.size(); i++) {
      SolrDocument document = resultList.get(i);
      String id = document.get("id").toString();
      String name = document.get("name") == null ? null : document.get("name").toString();
      String hilightName =
          document.get("hilightName") == null ? null : document.get("hilightName").toString();
      String brand = document.get("brand") == null ? null : document.get("brand").toString();
      String category =
          document.get("category") == null ? null : document.get("category").toString();
      String desc = document.get("desc") == null ? null : document.get("desc").toString();
      String imagePath =
          document.get("imagePath") == null ? null : document.get("imagePath").toString();
      String price = document.get("price") == null ? null : document.get("price").toString();
      if (resultHigh.get(id) != null) {
        if (resultHigh.get(id).get("name") != null) {
          hilightName = resultHigh.get(id).get("name").get(0);
        }
      }
      GoodsEntity goods = new GoodsEntity();
      goods.setId(id);
      goods.setBrand(brand);
      goods.setCategory(category);
      goods.setDesc(desc);
      goods.setImagePath(imagePath);
      goods.setName(hilightName);
      goods.setPrice(new BigDecimal(price));
      list.add(goods);
    }
  }

  /**
   * 根据搜索条件从solr查询数据.
   * 
   * @param conditon 搜索条件
   * @param orderByFiled 排序字段
   * @param ascOrDesc 升序降序
   * @param list 返回的查询结果
   * @param pageable 分页
   * @return SolrDocumentList
   */
  private Map<String, Object> getListPage(String conditon, String orderByFiled, String ascOrDesc,
      Pageable pageable) {
    List<GoodsEntity> list = new ArrayList<>();
    Map<String, Object> map = new HashMap<String, Object>();
    Map<String, Object> pageParam = new HashMap<String, Object>();
    SolrQuery query = new SolrQuery();
    // 指定查询结果返回哪些字段。多个时以逗号“,”分隔。不指定时，默认全返回
    query.setParam("fl", "*,score");
    // 开启高亮组件
    query.setHighlight(true);
    // 设置高亮前缀
    query.setHighlightSimplePre("<font color='red'>");
    // 设置高亮后缀
    query.setHighlightSimplePost("</font>");
    // 高亮字段：name
    query.addHighlightField("name");
    // 摘要信息的长度。默认值是100，这个长度是出现关键字的位置向前移6个字符，再往后100个字符，取这一段文本。
    query.setHighlightFragsize(100);
    // 返回高亮摘要的段数，默认值为1
    query.setHighlightSnippets(1);
    // 分片
    query.setFacet(true).setFacetMinCount(1).addFacetField("category");// 分片字段
    // 当前页，每页大小，总页数，总条数（默认值）
    int currentPage = 0, pageSize = 10, totalPage = 1, total = 0;
    if (pageable != null) {
      currentPage = pageable.getPageNumber();
      pageSize = pageable.getPageSize();
      query.setStart(currentPage * pageSize);
      query.setRows(pageSize);
    }
    logger.info(query.getStart() + "_" + query.getRows());
    pageParam.put("currentPage", currentPage);
    pageParam.put("pageSize", pageSize);
    if (StringUtils.isBlank(conditon)) {
      query.setQuery("*:*");
    } else {
      query.setQuery(conditon);
    }
    if (StringUtils.isBlank(orderByFiled) || StringUtils.isBlank(ascOrDesc)) {
      query.addSort("score", ORDER.desc);
    } else {
      query.addSort(orderByFiled, ORDER.valueOf(ascOrDesc));
    }
    // 结果
    QueryResponse response = null;
    SolrDocumentList resultList = null;
    try {
      response = solrClient.query(query);
      resultList = response.getResults();
      if (resultList != null && resultList.getNumFound() > 0) {
        // 获取solr里面查询到的数据
        getListFromSolr(response, resultList, list);
        // 总条数
        total = Integer.valueOf(String.valueOf(resultList.getNumFound()));
        if (total > pageSize) {
          if (total % pageSize == 0) {
            totalPage = total / pageSize;
          } else {
            totalPage = total / pageSize + 1;
          }
        }
      }
      pageParam.put("total", total);
      pageParam.put("totalPage", totalPage);
      List<FacetField> facets = response.getFacetFields();// 返回的facet列表
      Map<String, Object> categorys = new HashMap<String, Object>();
      for (FacetField facet : facets) {
        logger.info("facets field:" + facet.getName());
        List<Count> counts = facet.getValues();
        for (Count count : counts) {
          categorys.put(count.getName(), count.getCount());
        }
      }
      map.put("category", categorys);
      map.put("goods", list);
      map.put("pageParam", pageParam);
    } catch (Exception e) {
      e.printStackTrace();
      logger.error(e + "_" + e.getMessage());
    }
    return map;
  }

  @Override
  public Map<String, Object> query(String keyWord, Map<String, Object> map, String orderByFiled,
      String ascOrDesc, Pageable pageable) {
    // 搜索条件
    String condition = getSearchCondtion(keyWord, map);
    Map<String, Object> infos = getListPage(condition, orderByFiled, ascOrDesc, pageable);
    infos.put("keyWord", keyWord);
    infos.put("otherSearchMap", map);
    return infos;
  }

}
