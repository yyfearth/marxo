data = [
  value: 30,
  color: "#D41400"
,
  value: 50,
  color: "#00C0F7"
,
  value: 100,
  color: '#3B5998'
]

options =
  segmentShowStroke : true
  segmentStrokeColor : "#fff"
  segmentStrokeWidth : 2
  animation : true
  animationSteps : 100
  animationEasing : "easeOutElastic"
  animateRotate : true
  animateScale : true

context = document.getElementById('chart1').getContext('2d')
chart = new Chart(context).Pie(data, options)

data =
  labels: ["January", "February", "March", "April", "May", "June", "July"]
  datasets: [
    fillColor: "rgba(220,220,220,0.5)"
    strokeColor: "#D41400"
    pointColor: "#D41400"
    pointStrokeColor: "#fff"
    data: [19, 83, 48, 45, 32, 72, 30]
  ,
    fillColor: "rgba(220,220,220,0.5)"
    strokeColor: "#00C0F7"
    pointColor: "#00C0F7"
    pointStrokeColor: "#fff"
    data: [64, 73, 28, 64, 15, 12, 50]
  ,
    fillColor: "rgba(220,220,220,0.5)"
    strokeColor: "#3B5998"
    pointColor: "#3B5998"
    pointStrokeColor: "#fff"
    data: [80, 83, 51, 88, 102, 99, 100]
  ]

options =
  scaleOverlay: off
  scaleShowLabels: on
#  scaleOverride: on
  scalesSteps: 10
  onAnimationComplete: ->
    console.log 'hi'

context = document.getElementById('chart2').getContext('2d')
chart = new Chart(context).Line(data, options)
