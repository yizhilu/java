import React from 'react'
import { render } from 'react-dom'
import { Router, browserHistory } from 'react-router'

import withExampleBasename from '../withExampleBasename'
import './stubs/COURSES'

const rootRoute = {
  childRoutes: [ {
    path: '/',
    component: require('./home'),
    childRoutes: [
      require('./routes/Calendar'),
      require('./routes/Course'),
      require('./routes/Grades'),
      require('./routes/Messages'),
      require('./routes/Profile')
    ]
  } ]
}

render((
  <Router
    history={withExampleBasename(browserHistory, __dirname)}
    routes={rootRoute}/>
), document.getElementById('root'))