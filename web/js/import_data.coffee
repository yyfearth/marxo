"use strict"

define 'import_data', ['models'], ({
Workflow, Workflows
}) ->

  data =
    workflows: [
      name: 'Conference Check-in Mobile App'
      key: 'conf_app_dev'
      desc: 'Based on project requirements, use crowds sourcing to complete the development steps from ' +
      'design, implementation, testing, until the final result of this app. '
      start_node_id: 0
      nodes: [
        name: 'Requirement and Desgin'
        key: 'desgin'
        desc: 'Post requirement and collect desgins.'
        offset:
          x: 120, y: 30
        actions: [
          type: 'PAGE'
          name: 'Post Reqirement'
          key: 'post_req'
          content:
            type: 'PAGE'
            name: 'Conference Check-in Mobile App Reqirements and Design Submission'
            desc: ''
            sections: [
              name: 'Design Name'
              desc: 'The name of your app.'
              type: 'TEXT'
              options:
                required: true
                text_multiline: false
            ,
              name: 'Group Name'
              desc: 'The name of your development team or yourself.'
              type: 'TEXT'
              options:
                required: true
                text_multiline: false
            ,
              name: 'Team Members'
              desc: '''Your team members. One person per line with "First-name Last-name &lt;email&gt;" format.
              You can leave it blank if you are individual developer.'''
              type: 'TEXT'
              options:
                required: false
                text_multiline: true
            ,
              name: 'Design Introduction'
              desc: 'Give a brief introduction of your design.'
              type: 'HTML'
              options:
                required: true
            ,
              name: 'Upload Image'
              desc: 'Upload the image of your design. The image should be about 400x300px, not too big, not too small.'
              type: 'FILE'
              options:
                required: true
                file_accept: 'image/*'
            ,
              name: 'Upload Design Package'
              desc: 'Upload your design package, you should including ALL YOUR FILES in a ZIP/RAR/7Z format package.'
              type: 'FILE'
              options:
                required: true
            ,
              name: 'Comments'
              desc: 'Leave your comments if you want.'
              type: 'TEXT'
              options:
                required: false
                text_multiline: true
            ]
          tracking:
            duration: 2592000000 # 30 days
          event:
            duration: 0
        ,
          type: 'FACEBOOK'
          name: 'Post Requirement to Facebook'
          key: 'post_req_fb'
          content:
            type: 'FACEBOOK'
            message: '''Attention! Conference Check-in Mobile App start to receive submission.
            See requirements and submit your desgin in http://marxosys.ml/#id'''
          tracking:
            duration: 2592000000 # 30 days
          event:
            duration: 0
        ,
          type: 'WAIT'
          name: 'Wait for Design Submit'
          key: 'wait_for_design'
          event:
            duration: 864000000 # 10 days
        ]
      ,
        name: 'Vote App Design'
        key: 'vote'
        desc: 'Public vote submitted designs.'
        offset:
          x: 340, y: 30
        actions: [
          type: 'PAGE'
          name: 'Post Design Vote Form'
          key: 'post_design_vote_form'
          content:
            type: 'PAGE'
            name: 'Conference Check-in Mobile App Vote Your Favorite Design'
            desc: ''
            sections: [
              name: 'Vote Design'
              desc: 'Vote your favorite design.'
              type: 'RADIO'
              options:
                required: true
                gen_from_submission: 'post_req'
            ,
              name: 'Comments'
              desc: 'Leave your comments about the design you select and for all designs if you want.'
              type: 'TEXT'
              options:
                required: false
                text_multiline: true
            ]
          tracking:
            duration: 2592000000 # 30 days
          event:
            duration: 0
        ,
          type: 'FACEBOOK'
          name: 'Post Vote to Facebook'
          key: 'design_vote_fb'
          content:
            type: 'FACEBOOK'
            message: 'Vote your favorite desgin for our Conference Check-in Mobile App.\nhttp://marxosys.ml/#id'
          tracking:
            duration: 2592000000 # 30 days
          event:
            duration: 0
        ,
          type: 'WAIT'
          name: 'Wait for Design Vote'
          key: 'wait_for_desgin_vote'
          event:
            duration: 864000000 # 10 days
        ]
      ,
        name: 'App Implemetation'
        key: 'implemetation'
        desc: 'Post final design and wait for implemetation submission.'
        offset:
          x: 510, y: 30
        actions: [
          type: 'WAIT'
          name: 'Wait Owner Evaluate and Choose Final Desgin'
          desc: '''The project owner should select the final design.
          And process the chosen desgin, prepare to post it on to the page.'''
          key: 'proc_design'
          event:
            duration: 864000000 # 10 days
        ,
          type: 'PAGE'
          name: 'Post Final Desgin and Implement Submit Form'
          key: 'post_design_submit_impl'
          content:
            type: 'PAGE'
            name: 'Conference Check-in Mobile App Final Desgin and Implementation Submission'
            desc: '(The final desgin post in here)'
            sections: [
              name: 'Implementation Name'
              desc: 'Give a name to your app implementation.'
              type: 'TEXT'
              options:
                required: true
                text_multiline: false
            ,
              name: 'Group Name'
              desc: 'The name of your development team or yourself.'
              type: 'TEXT'
              options:
                required: true
                text_multiline: false
            ,
              name: 'Team Members'
              desc: '''Your team members. One person per line with "First-name Last-name &lt;email&gt;" format.
              You can leave it blank if you are individual developer.'''
              type: 'TEXT'
              options:
                required: false
                text_multiline: true
            ,
              name: 'Implemetation Description'
              desc: 'Give a detailed description for your implemetation.'
              type: 'HTML'
              options:
                required: true
            ,
              name: 'Upload Screenshot'
              desc: '''Upload the screenshot of your app to demonstrate your implementation.
              You can combine multiple screenshots into one image if you like.
              The image should be about 600x400px, not too big, not too small.'''
              type: 'FILE'
              options:
                required: true
                file_accept: 'image/*'
            ,
              name: 'Upload Implementation Package with Source Code'
              desc: '''Upload your implemetation package,
              you should including ALL YOUR FILES including scource code and necessary binaries in a ZIP/RAR/7Z format package.'''
              type: 'FILE'
              options:
                required: true
            ,
              name: 'Comments'
              desc: 'Leave your comments if you want.'
              type: 'TEXT'
              options:
                required: false
                text_multiline: true
            ]
          tracking:
            duration: 2592000000 # 30 days
          event:
            duration: 0
        ,
          type: 'FACEBOOK'
          name: 'Post Design to Facebook'
          key: 'desgin_impl_fb'
          content:
            type: 'FACEBOOK'
            message: '''The final desgin of Conference Check-in Mobile App is ready!
            Please submit your code in http://marxosys.ml/#id'''
          tracking:
            duration: 2592000000 # 30 days
          event:
            duration: 0
        ,
          type: 'WAIT'
          name: 'Wait for Implementation'
          key: 'wait_for_impl'
          event:
            duration: 864000000 # 10 days
        ]
      ,
        name: 'Review'
        key: 'review'
        desc: 'Evaluator review submitted app implemetations.'
        offset:
          x: 700, y: 30
        actions: [
          type: 'PAGE'
          name: 'Post Review Form'
          key: 'review_page'
          content:
            type: 'PAGE'
            name: 'Conference Check-in Mobile App Implemetation Review'
            desc: ''
            sections: [
              name: 'Choose Design'
              desc: 'Choose the best implemetation you think.'
              type: 'RADIO'
              options:
                required: true
                gen_from_submission: 'post_design_submit_impl'
            ,
              name: 'Comments'
              desc: 'Leave your comments about the design you chose.'
              type: 'TEXT'
              options:
                required: true
                text_multiline: true
            ]
          tracking:
            duration: 2592000000 # 30 days
          event:
            duration: 864000000 # 10 days
        ]
      ,
        name: 'Testing'
        key: 'testing'
        desc: 'Post selected implementaion and open beta testing, collect feedbacks.'
        offset:
          x: 840, y: 30
        actions: [
          type: 'WAIT'
          name: 'Wait Owner Process Review and Implementation'
          key: 'proc_impl'
          event:
            duration: 864000000 # 10 days
        ,
          type: 'PAGE'
          name: 'Post Selected Implementation and Test Request'
          key: 'post_impl_test'
          content:
            type: 'PAGE'
            name: 'Conference Check-in Mobile App Implementation and Test Request'
            desc: '''<p>(Post selected implementation description here)</p>
            <p>Please fire any bug you find here to help us to improve the quality.</p>'''
            sections: [
              name: 'Bug Name'
              desc: 'Give a name to the bug as a short description.'
              type: 'TEXT'
              options:
                required: true
                text_multiline: false
            ,
              name: 'Bug Description'
              desc: 'Give a more detailed bug description.'
              type: 'HTML'
              options:
                required: true
            ,
              name: 'Upload Screenshot'
              desc: 'Upload a screenshot for the bug. You can combine multiple screenshots into one image to upload.'
              type: 'FILE'
              options:
                required: false
                file_accept: 'image/*'
            ,
              name: 'Upload Attachment'
              desc: 'Any attachment if you want to provide. If you have multiple attachments, you can compress them into a ZIP/RAR/7Z package to upload.'
              type: 'FILE'
              options:
                required: false
            ]
          tracking:
            duration: 2592000000 # 30 days
          event:
            duration: 0
        ,
          type: 'FACEBOOK'
          name: 'Post Implementation to Facebook'
          key: 'impl_fb'
          content:
            type: 'FACEBOOK'
            message: '''Our committee have the final result of the winner of implemetation of our app.
            Welcome to feedback any bugs or leave comments in http://marxosys.ml/#id'''
          tracking:
            duration: 2592000000 # 30 days
          event:
            duration: 0
        ,
          type: 'WAIT'
          name: 'Wait for Testing'
          key: 'wait_for_test'
          event:
            duration: 864000000 # 10 days
        ]
      ,
        name: 'Demo'
        key: 'demo'
        desc: 'Post final result to the public.'
        offset:
          x: 980, y: 30
        actions: [
          type: 'WAIT'
          name: 'Wait Owner Final Result'
          key: 'wait_for_result'
          event:
            duration: 864000000 # 10 days
        ,
          type: 'PAGE'
          name: 'Post Final Result and Demo'
          key: 'post_result'
          content:
            type: 'PAGE'
            name: 'Conference Check-in Mobile App Demo'
            desc: ''
            sections: [
              name: 'Rating'
              desc: 'Rating the app demo.'
              type: 'RADIO'
              options:
                required: true
                manual_options: ['1 ★', '2 ★★', '3 ★★★', '4 ★★★★', '5 ★★★★★']
            ,
              name: 'Comments'
              desc: 'Leave your comments about the demo of the app.'
              type: 'TEXT'
              options:
                required: false
                text_multiline: true
            ]
          tracking:
            duration: 2592000000 # 30 days
          event:
            duration: 0
        ,
          type: 'FACEBOOK'
          name: 'Post Final Result to Facebook'
          key: 'result_fb'
          content:
            type: 'FACEBOOK'
            message: 'The Conference Check-in Mobile App is ready!\nSee our demo here http://marxosys.ml/#id'
          tracking:
            duration: 2592000000 # 30 days
          event:
            duration: 0
        ]
      ]
      links: [
        key: 'desgin_to_vote'
        prev_node_id: 0
        next_node_id: 1
      ,
        key: 'vote_to_implemetation'
        prev_node_id: 1
        next_node_id: 2
      ,
        key: 'implemetation_to_review'
        prev_node_id: 2
        next_node_id: 3
      ,
        key: 'review_to_testing'
        prev_node_id: 3
        next_node_id: 4
      ,
        key: 'testing_to_demo'
        prev_node_id: 4
        next_node_id: 5
      ]
    ]

  console.log 'data start importing...'

  (new Workflows data.workflows).forEach (wf) -> wf.save()

  return
