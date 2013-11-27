"use strict"

define 'test_data', ['models'], (models) ->

  data =
    tenants: [
      id: 0
      name: 'Marxo'
      desc: 'Marxo dev group'
      contact: 'Wilson Young'
      email: 'wilson@gmail.com'
      tel: '(408) 888-8888'
      fax: '(408) 888-8888'
      addr: 'One Washington Square, San Jose, CA 95112'
      created_at: '2013-09-05T02:18:13.621Z'
      updated_at: '2013-09-05T02:18:13.621Z'
    ]
    publishers: [
      email: 'test@example.com'
      password: 'B4driGpKjDrtdKaAoA8nUmm+D2Pl3kxoF5POX0sGSk4' # test
      first_name: 'Test'
      last_name: 'User'
      tenant_id: 0
      created_at: '2013-09-05T02:18:13.621Z'
      updated_at: '2013-09-05T02:18:13.621Z'
    ,
      email: 'yyfearth@gmail.com'
      password: '2k96H29ECsJ05BJAkEGm6FC+UgjwVTc1qOd7SGG2uS8' # asdfasdf
      first_name: 'Wilson'
      last_name: 'Young'
      tenant_id: 0
      created_at: '2013-09-05T02:18:13.621Z'
      updated_at: '2013-09-05T02:18:13.621Z'
    ,
      email: 'otaru14204@hotmail.com'
      password: 'XELXdnuv/p7QeCzPM7Pl7TLfd6o2NZSaPb/sGtYUg5Q' # @qwer123
      first_name: 'Leo'
      last_name: 'Chu'
      tenant_id: 0
      created_at: '2013-09-05T02:18:13.621Z'
      updated_at: '2013-09-05T02:18:13.621Z'
    ]
    workflows: [
      id: '655a3dd88b20999dd786d38b'
      name: 'Waterfall Development'
      key: 'waterfall_dev'
      desc: 'Workflow for Waterfall Development'
      start_node_id: 'ec21b6896dd7a39b2e4ae470'
      nodes: [
        id: 'ec21b6896dd7a39b2e4ae470'
        name: 'Requirement'
        key: 'requirement'
        offset:
          x: 24, y: 41.5
        actions: [
          id: 'ec21b6896dd7a39b2e4ae471'
          type: 'CREATE_PAGE'
          name: 'Post Page or Form'
          key: 'create_page'
        ,
          type: 'POST_FACEBOOK'
          name: 'Post to Facebook'
          key: 'post_facebook'
        ,
          type: 'MAKE_SCHEDULE'
          name: 'Make Schedule'
          key: 'make_schedule'
        ,
          type: 'GENERATE_REPORT'
          name: 'Generate Report'
          key: 'generate_report'
        ]
        created_at: '2013-11-12T01:31:52.328Z'
        updated_at: '2013-11-12T01:31:52.328Z'
      ,
        id: 'c4ebf77d7d3564cc60d322e7'
        name: 'Design'
        key: 'design'
        offset:
          x: 192, y: 41
        actions: [
          type: 'CREATE_PAGE'
          name: 'Post Page or Form'
          key: 'create_page'
        ,
          type: 'POST_FACEBOOK'
          name: 'Post to Facebook'
          key: 'post_facebook'
        ,
          type: 'SEND_EMAIL'
          name: 'Send Email'
          key: 'send_email'
        ,
          type: 'MAKE_SCHEDULE'
          name: 'Make Schedule'
          key: 'make_schedule'
          start_type: 'manual'
        ,
          type: 'GENERATE_REPORT'
          name: 'Generate Report'
          key: 'generate_report'
        ]
        created_at: '2013-11-12T01:49:10.224Z'
        updated_at: '2013-11-12T01:49:10.224Z'
      ,
        id: '6842887a4153d55ed1c5b228'
        name: 'Implementation'
        key: 'implementation'
        offset:
          x: 338.21875, y: 41.5
        actions: [
          type: 'CREATE_PAGE'
          name: 'Post Page or Form'
          key: 'create_page'
        ,
          type: 'POST_FACEBOOK'
          name: 'Post to Facebook'
          key: 'post_facebook'
        ,
          type: 'SEND_EMAIL'
          name: 'Send Email'
          key: 'send_email'
        ,
          type: 'MAKE_SCHEDULE'
          name: 'Make Schedule'
          key: 'make_schedule'
          start_type: 'manual'
        ,
          type: 'GENERATE_REPORT'
          name: 'Generate Report'
          key: 'generate_report'
        ]
        created_at: '2013-11-12T01:49:46.366Z'
        updated_at: '2013-11-12T01:49:46.366Z'
      ,
        id: '142720933a58ebd2d70110c5'
        name: 'Testing'
        key: 'testing'
        offset:
          x: 669, y: 41
        actions: [
          type: 'CREATE_PAGE'
          name: 'Post Page or Form'
          key: 'create_page'
        ,
          type: 'POST_FACEBOOK'
          name: 'Post to Facebook'
          key: 'post_facebook'
        ,
          type: 'SEND_EMAIL'
          name: 'Send Email'
          key: 'send_email'
        ,
          type: 'MAKE_SCHEDULE'
          name: 'Make Schedule'
          key: 'make_schedule'
          start_type: 'manual'
        ,
          type: 'GENERATE_REPORT'
          name: 'Generate Report'
          key: 'generate_report'
        ]
        created_at: '2013-11-12T01:50:13.818Z'
        updated_at: '2013-11-12T01:50:13.818Z'
      ,
        id: '4387684971cf7d8ae0e5111c'
        name: 'Demo'
        key: 'demo'
        offset:
          x: 814.21875, y: 41.5
        actions: [
          type: 'CREATE_PAGE'
          name: 'Post Page or Form'
          key: 'create_page'
        ,
          type: 'POST_FACEBOOK'
          name: 'Post to Facebook'
          key: 'post_facebook'
        ,
          type: 'SEND_EMAIL'
          name: 'Send Email'
          key: 'send_email'
        ,
          type: 'GENERATE_REPORT'
          name: 'Generate Report'
          key: 'generate_report'
        ]
        created_at: '2013-11-12T01:50:56.436Z'
        updated_at: '2013-11-12T01:50:56.436Z'
      ,
        id: '9ddf5927f60bed963c753283'
        name: 'Integration'
        key: 'integration'
        offset:
          x: 517.5625, y: 41.5
        workflow_id: '655a3dd88b20999dd786d38b'
        actions: [
          type: 'POST_FACEBOOK'
          name: 'Post to Facebook'
          key: 'post_facebook'
        ,
          type: 'SEND_EMAIL'
          name: 'Send Email'
          key: 'send_email'
        ,
          type: 'MAKE_SCHEDULE'
          name: 'Make Schedule'
          key: 'make_schedule'
          start_type: 'manual'
        ]
        created_at: '2013-11-12T03:42:32.728Z'
        updated_at: '2013-11-12T03:42:32.728Z'
      ]
      links: [
        id: '6d1378ca0858b307cc1145d2'
        key: 'requirement_to_design'
        prev_node_id: 'ec21b6896dd7a39b2e4ae470'
        next_node_id: 'c4ebf77d7d3564cc60d322e7'
        conditional: false
        created_at: '2013-11-12T01:49:16.420Z'
        updated_at: '2013-11-12T01:49:16.420Z'
      ,
        id: 'e89620d9fa1a49315e7af9b9'
        key: 'design_to_implementation'
        prev_node_id: 'c4ebf77d7d3564cc60d322e7'
        next_node_id: '6842887a4153d55ed1c5b228'
        conditional: false
        created_at: '2013-11-12T01:49:50.782Z'
        updated_at: '2013-11-12T01:49:50.782Z'
      ,
        id: '59623f83efccd3d9c190a521'
        key: 'testing_to_demo'
        prev_node_id: '142720933a58ebd2d70110c5'
        next_node_id: '4387684971cf7d8ae0e5111c'
        conditional: false
        created_at: '2013-11-12T01:51:07.066Z'
        updated_at: '2013-11-12T01:51:07.066Z'
      ,
        id: '0c97f0018cbdbd3f465d6f30'
        workflow_id: '655a3dd88b20999dd786d38b'
        key: 'implementation_to_integration'
        prev_node_id: '6842887a4153d55ed1c5b228'
        next_node_id: '9ddf5927f60bed963c753283'
        conditional: false
        created_at: '2013-11-12T03:42:36.785Z'
        updated_at: '2013-11-12T03:42:36.785Z'
      ,
        id: '6eac0542472d22acd92dc757'
        workflow_id: '655a3dd88b20999dd786d38b'
        key: 'integration_to_testing'
        prev_node_id: '9ddf5927f60bed963c753283'
        next_node_id: '142720933a58ebd2d70110c5'
        conditional: false
        created_at: '2013-11-12T03:42:43.499Z'
        updated_at: '2013-11-12T03:42:43.499Z'
      ]
      created_at: '2013-11-12T01:29:33.302Z'
      updated_at: '2013-11-12T01:29:33.302Z'
    ,
      id: '50447afb4728cb2036cf9ca0'
      name: 'Demo Workflow'
      key: 'demo_workflow'
      desc: 'Demo Workflow for App Dev and Logo Desgin'
      start_node_id: '50447afb4728cb2036cf9cb0'
      nodes: [
        id: '50447afb4728cb2036cf9cb0'
        name: 'Post Idea'
        key: 'post_idea'
        desc: 'Post project idea'
        created_at: '2013-06-27T00:23:31.747Z'
        updated_at: '2013-06-27T00:23:31.747Z'
        offset:
          x: 26, y: 43
        actions: [
          id: '50447afb4728cb2036cf9f00'
          type: 'CREATE_PAGE'
        ,
          id: '50447afb4728cb2036cf9f01'
          type: 'POST_FACEBOOK'
        ,
          id: '50447afb4728cb2036cf9f02'
          type: 'GENERATE_REPORT'
        ]
      ,
        id: '50447afb4728cb2036cf9cb1'
        name: 'Post Requirements'
        key: 'post_requirements'
        desc: 'Post requirements'
        created_at: '2013-06-27T00:24:55.070Z'
        updated_at: '2013-06-27T00:24:55.070Z'
        offset:
          x: 334, y: 44
        actions: [
          id: '50447afb4728cb2036cf9f03'
          type: 'CREATE_PAGE'
        ,
          id: '50447afb4728cb2036cf9ce4'
          type: 'POST_FACEBOOK'
        ,
          id: '50447afb4728cb2036cf9f05'
          type: 'GENERATE_REPORT'
        ]
      ,
        id: '50447afb4728cb2036cf9cb2'
        name: 'Cancel Notification'
        key: 'cancel_notification'
        desc: 'Cancel project for less response than expected'
        created_at: '2013-06-27T00:25:58.702Z'
        updated_at: '2013-06-27T00:25:58.702Z'
        offset:
          x: 334, y: 204
        actions: [
          id: '50447afb4728cb2036cf9f06'
          type: 'POST_FACEBOOK'
        ]
      ,
        id: '50447afb4728cb2036cf9cb3'
        name: 'Retrieve App Submissions'
        key: 'retrieve_app_submissions'
        desc: 'Retrieve app submissions'
        created_at: '2013-06-27T00:32:46.817Z'
        updated_at: '2013-06-27T00:32:46.817Z'
        offset:
          x: 613, y: 44
        actions: [
          id: '50447afb4728cb2036cf9f07'
          type: 'CREATE_PAGE'
        ,
          id: '50447afb4728cb2036cf9f08'
          type: 'POST_FACEBOOK'
        ,
          id: '50447afb4728cb2036cf9f09'
          type: 'GENERATE_REPORT'
        ]
      ,
        id: '50447afb4728cb2036cf9cb4'
        name: 'Retrieve Logo Design'
        key: 'retrieve_logo_design'
        desc: 'Retrieve logo design'
        created_at: '2013-06-27T00:35:36.856Z'
        updated_at: '2013-06-27T00:35:36.856Z'
        offset:
          x: 629, y: 282
        actions: [
          id: '50447afb4728cb2036cf9f0a'
          type: 'CREATE_PAGE'
        ,
          id: '50447afb4728cb2036cf9f0b'
          type: 'POST_FACEBOOK'
        ,
          id: '50447afb4728cb2036cf9f0c'
          type: 'GENERATE_REPORT'
        ]
      ,
        id: '50447afb4728cb2036cf9cb5'
        name: 'Email to Evaluators'
        key: 'email_to_evaluators'
        desc: 'Email to evaluators'
        created_at: '2013-06-27T00:36:21.950Z'
        updated_at: '2013-06-27T00:36:21.950Z'
        offset:
          x: 1018, y: 44
        actions: [
          id: '50447afb4728cb2036cf9f0d'
          type: 'CREATE_PAGE'
        ,
          id: '50447afb4728cb2036cf9f0e'
          type: 'SEND_EMAIL'
        ,
          id: '50447afb4728cb2036cf9f0f'
          type: 'GENERATE_REPORT'
        ]
      ,
        id: '50447afb4728cb2036cf9cb6'
        name: 'Post and Vote'
        key: 'post_and_vote'
        desc: 'Post and vote'
        created_at: '2013-06-27T00:36:47.097Z'
        updated_at: '2013-06-27T00:36:47.097Z'
        offset:
          x: 1043, y: 282
        actions: [
          id: '50447afb4728cb2036cf9f10'
          type: 'CREATE_PAGE'
        ,
          id: '50447afb4728cb2036cf9f11'
          type: 'POST_FACEBOOK'
        ,
          id: '50447afb4728cb2036cf9f12'
          type: 'GENERATE_REPORT'
        ]
      ,
        id: '50447afb4728cb2036cf9cb7'
        name: 'Post Final Result and Reward'
        key: 'post_final_result_and_reward'
        desc: 'Post final result and reward'
        created_at: '2013-06-27T00:39:04.602Z'
        updated_at: '2013-06-27T00:39:04.602Z'
        offset:
          x: 1420, y: 183
        actions: [
          id: '50447afb4728cb2036cf9f13'
          type: 'CREATE_PAGE'
        ,
          id: '50447afb4728cb2036cf9f14'
          type: 'POST_FACEBOOK'
        ,
          id: '50447afb4728cb2036cf9f15'
          type: 'SEND_EMAIL'
        ,
          id: '50447afb4728cb2036cf9f16'
          type: 'GENERATE_REPORT'
        ]
      ]
      links: [
        id: '50447afb4728cb2036cf9cc0'
        key: '10_days_likes_gte_300'
        prev_node_id: '50447afb4728cb2036cf9cb0'
        next_node_id: '50447afb4728cb2036cf9cb1'
        name: '10 Days & Likes >= 300'
        created_at: '2013-06-27T00:27:31.946Z'
        updated_at: '2013-06-27T00:27:31.946Z'
      ,
        id: '50447afb4728cb2036cf9cc1'
        key: '10_days_likes_lt_300'
        prev_node_id: '50447afb4728cb2036cf9cb0'
        next_node_id: '50447afb4728cb2036cf9cb2'
        name: '10 Days & Likes < 300'
        created_at: '2013-06-27T00:28:58.636Z'
        updated_at: '2013-06-27T00:28:58.636Z'
      ,
        id: '50447afb4728cb2036cf9cc2'
        key: 'post_req_to_retrieve_app'
        prev_node_id: '50447afb4728cb2036cf9cb1'
        next_node_id: '50447afb4728cb2036cf9cb3'
        created_at: '2013-06-27T00:33:01.670Z'
        updated_at: '2013-06-27T00:33:01.670Z'
      ,
        id: '50447afb4728cb2036cf9cc3'
        key: 'post_req_to_retrieve_logo'
        prev_node_id: '50447afb4728cb2036cf9cb1'
        next_node_id: '50447afb4728cb2036cf9cb4'
        created_at: '2013-06-27T00:35:45.912Z'
        updated_at: '2013-06-27T00:35:45.912Z'
      ,
        id: '50447afb4728cb2036cf9cc4'
        key: 'retrieve_app_to_evaluate'
        prev_node_id: '50447afb4728cb2036cf9cb3'
        next_node_id: '50447afb4728cb2036cf9cb5'
        name: '15 Days & > 80% Response'
        created_at: '2013-06-27T00:37:24.163Z'
        updated_at: '2013-06-27T00:37:24.163Z'
      ,
        id: '50447afb4728cb2036cf9cc5'
        key: 'retrieve_logo_to_post_and_vote'
        prev_node_id: '50447afb4728cb2036cf9cb4'
        next_node_id: '50447afb4728cb2036cf9cb6'
        name: '10 Days & Submissions >= 3'
        created_at: '2013-06-27T00:37:43.706Z'
        updated_at: '2013-06-27T00:37:43.706Z'
      ,
        id: '50447afb4728cb2036cf9cc6'
        name: '(fallback)'
        key: 'faild_to_evaluate'
        desc: 'Manual trigger back if failed to evaluate'
        prev_node_id: '50447afb4728cb2036cf9cb5'
        next_node_id: '50447afb4728cb2036cf9cb3'
        created_at: '2013-06-27T00:37:51.943Z'
        updated_at: '2013-06-27T00:37:51.943Z'
      ,
        id: '50447afb4728cb2036cf9cc7'
        name: '(fallback)'
        key: 'failed_to_vote'
        desc: 'Manual trigger back if failed to vote'
        prev_node_id: '50447afb4728cb2036cf9cb6'
        next_node_id: '50447afb4728cb2036cf9cb4'
        created_at: '2013-06-27T00:38:05.360Z'
        updated_at: '2013-06-27T00:38:05.360Z'
      ,
        id: '50447afb4728cb2036cf9cc8'
        key: 'evaluate_to_post_final'
        prev_node_id: '50447afb4728cb2036cf9cb5'
        next_node_id: '50447afb4728cb2036cf9cb7'
        created_at: '2013-06-27T00:39:22.841Z'
        updated_at: '2013-06-27T00:39:22.841Z'
      ,
        id: '50447afb4728cb2036cf9cc9'
        key: 'post_and_vote_to_post_final'
        prev_node_id: '50447afb4728cb2036cf9cb6'
        next_node_id: '50447afb4728cb2036cf9cb7'
        created_at: '2013-06-27T00:39:26.674Z'
        updated_at: '2013-06-27T00:39:26.674Z'
      ]
      created_at: '2013-06-27T00:22:20.272Z'
      updated_at: '2013-06-27T00:22:20.272Z'
    ,
      id: '50447afb4728cb2036cf9ca1'
      key: 'test_wf'
      name: 'Test Workflow'
      desc: 'The test workflow'
      created_at: new Date(1363879373649)
      updated_at: new Date(1363879373649)
      nodes: [
        id: '507f81413d070321728fde10'
        key: 'post_idea'
        name: 'Post Idea'
        desc: 'Post software project ideas'
        workflow_id: '50447afb4728cb2036cf9ca1'
        actions: []
        created_at: new Date(1363879373649)
        updated_at: new Date(1363879373649)
      ,
        id: '507f81413d070321728fde11'
        key: 'post_cancel'
        name: 'Post Cancel'
        desc: 'Post cancel notification'
        workflow_id: '50447afb4728cb2036cf9ca1'
        actions: []
        created_at: new Date(1363879373649)
        updated_at: new Date(1363879373649)
      ,
        id: '507f81413d070321728fde12'
        key: 'post_req'
        name: 'Post Requirement'
        desc: 'Post project requirement'
        workflow_id: '50447afb4728cb2036cf9ca1'
        actions: []
        created_at: new Date(1363879373649)
        updated_at: new Date(1363879373649)
      ,
        id: '507f81413d070321728fde13'
        key: 'submit_design'
        name: 'Submit Design'
        desc: 'Retrieve theme design submissions & e-mail to stackholders'
        workflow_id: '50447afb4728cb2036cf9ca1'
        actions: []
        created_at: new Date(1363879373649)
        updated_at: new Date(1363879373649)
      ,
        id: '507f81413d070321728fde14'
        key: 'notification'
        name: 'Notification'
        desc: 'Notification'
        workflow_id: '50447afb4728cb2036cf9ca1'
        actions: []
        created_at: new Date(1363879373649)
        updated_at: new Date(1363879373649)
      ,
        id: '507f81413d070321728fde15'
        key: 'post_result'
        name: 'Post Result'
        desc: 'Post & e-mail result everyone'
        workflow_id: '50447afb4728cb2036cf9ca1'
        actions: []
        created_at: new Date(1363879373649)
        updated_at: new Date(1363879373649)
      ]
      links: [
        id: '507f81413d070321728fde22'
        key: 'post_req_to_submit_design'
        workflow_id: '50447afb4728cb2036cf9ca1'
        prev_node_id: '507f81413d070321728fde12'
        next_node_id: '507f81413d070321728fde13'
        created_at: new Date(1363879373649)
        updated_at: new Date(1363879373649)
      ,
        id: '507f81413d070321728fde24'
        key: 'pass_to_post'
        desc: 'Post & e-mail to everyone if pass rate > 50%'
        workflow_id: '50447afb4728cb2036cf9ca1'
        prev_node_id: '507f81413d070321728fde13'
        next_node_id: '507f81413d070321728fde15'
        created_at: new Date(1363879373649)
        updated_at: new Date(1363879373649)
      ,
        id: '507f81413d070321728fde20'
        key: 'to_cancel'
        name: 'Like < 300'
        desc: 'Cancel if like count < 300'
        workflow_id: '50447afb4728cb2036cf9ca1'
        prev_node_id: '507f81413d070321728fde10'
        next_node_id: '507f81413d070321728fde11'
        created_at: new Date(1363879373649)
        updated_at: new Date(1363879373649)
      ,
        id: '507f81413d070321728fde21'
        key: 'continue_to_req'
        name: 'Like >= 300'
        desc: 'Continue to post requirement if like count >= 300'
        workflow_id: '50447afb4728cb2036cf9ca1'
        prev_node_id: '507f81413d070321728fde10'
        next_node_id: '507f81413d070321728fde12'
        created_at: new Date(1363879402429)
        updated_at: new Date(1363879402429)
      ,
        id: '507f81413d070321728fde23'
        key: 'not_pass_to_notify'
        name: 'Pass rate <= 50%'
        desc: 'Notification if pass rate <= 50%'
        workflow_id: '50447afb4728cb2036cf9ca1'
        prev_node_id: '507f81413d070321728fde13'
        next_node_id: '507f81413d070321728fde14'
        created_at: new Date(1363879447134)
        updated_at: new Date(1363879447134)
      ]
      node_ids: [
        '507f81413d070321728fde10'
        '507f81413d070321728fde11'
        '507f81413d070321728fde12'
        '507f81413d070321728fde13'
        '507f81413d070321728fde14'
        '507f81413d070321728fde15'
      ]
      link_ids: [
        '507f81413d070321728fde20'
        '507f81413d070321728fde21'
        '507f81413d070321728fde22'
        '507f81413d070321728fde23'
        '507f81413d070321728fde24'
      ]
    ]

    projects: [
      id: '50447afb4728cc2036cf9ca0'
      template_id: '50447afb4728cb2036cf9ca0'
      name: 'Demo Project'
      key: 'demo_prj'
      desc: 'Demo Project from Demo Workflow'
      status: 'STARTED'
      nodes: [
        id: '50447afb4728cc2036cf9cb0'
        template_id: '50447afb4728cb2036cf9cb0'
        name: 'Post Idea'
        key: 'post_idea'
        desc: 'Post project idea'
        status: 'FINISHED'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
        offset:
          x: 26, y: 43
        actions: [
          id: '50447afb4728cc2036cf9f00'
          name: 'Create page'
          type: 'CREATE_PAGE'
          status: 'FINISHED'
        ,
          id: '50447afb4728cc2036cf9f01'
          name: 'Post to facebook'
          type: 'POST_FACEBOOK'
          status: 'FINISHED'
        ,
          id: '50447afb4728cc2036cf9f02'
          name: 'Generate report'
          type: 'GENERATE_REPORT'
          status: 'FINISHED'
        ]
      ,
        id: '50447afb4728cc2036cf9cb1'
        template_id: '50447afb4728cb2036cf9cb1'
        name: 'Post Requirements'
        key: 'post_requirements'
        desc: 'Post requirements'
        status: 'FINISHED'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
        offset:
          x: 334, y: 44
        actions: [
          id: '50447afb4728cc2036cf9f03'
          name: 'Create page'
          type: 'CREATE_PAGE'
          status: 'FINISHED'
        ,
          id: '50447afb4728cc2036cf9ce4'
          name: 'Post to facebook'
          type: 'POST_FACEBOOK'
          status: 'FINISHED'
        ,
          id: '50447afb4728cc2036cf9f05'
          name: 'Generate report'
          type: 'GENERATE_REPORT'
          status: 'FINISHED'
        ]
      ,
        id: '50447afb4728cc2036cf9cb2'
        template_id: '50447afb4728cb2036cf9cb2'
        name: 'Cancel Notification'
        key: 'cancel_notification'
        status: 'IDLE'
        desc: 'Cancel project for less response than expected'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
        offset:
          x: 334, y: 204
        actions: [
          id: '50447afb4728cc2036cf9f06'
          name: 'Post to facebook'
          type: 'POST_FACEBOOK'
          status: 'IDLE'
        ]
      ,
        id: '50447afb4728cc2036cf9cb3'
        template_id: '50447afb4728cb2036cf9cb3'
        name: 'Retrieve App Submissions'
        key: 'retrieve_app_submissions'
        desc: 'Retrieve app submissions'
        status: 'STARTED'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
        offset:
          x: 613, y: 44
        actions: [
          id: '50447afb4728cc2036cf9f07'
          name: 'Create page'
          type: 'CREATE_PAGE'
          status: 'FINISHED'
        ,
          id: '50447afb4728cc2036cf9f08'
          name: 'Post to facebook'
          type: 'POST_FACEBOOK'
          status: 'FINISHED'
        ,
          id: '50447afb4728cc2036cf9f09'
          name: 'Generate report'
          type: 'GENERATE_REPORT'
          status: 'STARTED'
        ]
      ,
        id: '50447afb4728cc2036cf9cb4'
        template_id: '50447afb4728cb2036cf9cb4'
        name: 'Retrieve Logo Design'
        key: 'retrieve_logo_design'
        desc: 'Retrieve logo design'
        status: 'FINISHED'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
        offset:
          x: 629, y: 282
        actions: [
          id: '50447afb4728cc2036cf9f0a'
          name: 'Create page'
          type: 'CREATE_PAGE'
          status: 'FINISHED'
        ,
          id: '50447afb4728cc2036cf9f0b'
          name: 'Post to facebook'
          type: 'POST_FACEBOOK'
          status: 'FINISHED'
        ,
          id: '50447afb4728cc2036cf9f0c'
          name: 'Generate report'
          type: 'GENERATE_REPORT'
          status: 'FINISHED'
        ]
      ,
        id: '50447afb4728cc2036cf9cb5'
        template_id: '50447afb4728cb2036cf9cb5'
        name: 'Email to Evaluators'
        key: 'email_to_evaluators'
        desc: 'Email to evaluators'
        status: 'IDLE'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
        offset:
          x: 1018, y: 44
        actions: [
          id: '50447afb4728cc2036cf9f0d'
          name: 'Create page'
          type: 'CREATE_PAGE'
          status: 'IDLE'
        ,
          id: '50447afb4728cc2036cf9f0e'
          name: 'Send email'
          type: 'SEND_EMAIL'
          status: 'IDLE'
        ,
          id: '50447afb4728cc2036cf9f0f'
          name: 'Generate report'
          type: 'GENERATE_REPORT'
          status: 'IDLE'
        ]
      ,
        id: '50447afb4728cc2036cf9cb6'
        template_id: '50447afb4728cb2036cf9cb6'
        name: 'Post and Vote'
        key: 'post_and_vote'
        desc: 'Post and vote'
        status: 'STARTED'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
        offset:
          x: 1043, y: 282
        actions: [
          id: '50447afb4728cc2036cf9f10'
          name: 'Create page'
          type: 'CREATE_PAGE'
          status: 'FINISHED'
        ,
          id: '50447afb4728cc2036cf9f11'
          name: 'Post to facebook'
          type: 'POST_FACEBOOK'
          status: 'FINISHED'
        ,
          id: '50447afb4728cc2036cf9f12'
          name: 'Generate report'
          type: 'GENERATE_REPORT'
          status: 'STARTED'
        ]
      ,
        id: '50447afb4728cc2036cf9cb7'
        template_id: '50447afb4728cb2036cf9cb7'
        name: 'Post Final Result and Reward'
        key: 'post_final_result_and_reward'
        desc: 'Post final result and reward'
        status: 'IDLE'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
        offset:
          x: 1420, y: 183
        actions: [
          id: '50447afb4728cc2036cf9f13'
          name: 'Create page'
          type: 'CREATE_PAGE'
          status: 'IDLE'
        ,
          id: '50447afb4728cc2036cf9f14'
          name: 'Post to facebook'
          type: 'POST_FACEBOOK'
          status: 'IDLE'
        ,
          id: '50447afb4728cc2036cf9f15'
          name: 'Send email'
          type: 'SEND_EMAIL'
          status: 'IDLE'
        ,
          id: '50447afb4728cc2036cf9f16'
          name: 'Generate report'
          type: 'GENERATE_REPORT'
          status: 'IDLE'
        ]
      ]
      links: [
        id: '50447afb4728cc2036cf9cc0'
        template_id: '50447afb4728cb2036cf9cc0'
        key: '10_days_likes_gte_300'
        prev_node_id: '50447afb4728cc2036cf9cb0'
        next_node_id: '50447afb4728cc2036cf9cb1'
        name: '10 Days & Likes >= 300'
        status: 'PASSED'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '50447afb4728cc2036cf9cc1'
        template_id: '50447afb4728cb2036cf9cc1'
        key: '10_days_likes_lt_300'
        prev_node_id: '50447afb4728cc2036cf9cb0'
        next_node_id: '50447afb4728cc2036cf9cb2'
        name: '10 Days & Likes < 300'
        status: 'FAILED'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '50447afb4728cc2036cf9cc2'
        template_id: '50447afb4728cb2036cf9cc2'
        key: 'post_req_to_retrieve_app'
        prev_node_id: '50447afb4728cc2036cf9cb1'
        next_node_id: '50447afb4728cc2036cf9cb3'
        status: 'PASSED'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '50447afb4728cc2036cf9cc3'
        template_id: '50447afb4728cb2036cf9cc3'
        key: 'post_req_to_retrieve_logo'
        prev_node_id: '50447afb4728cc2036cf9cb1'
        next_node_id: '50447afb4728cc2036cf9cb4'
        status: 'PASSED'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '50447afb4728cc2036cf9cc4'
        template_id: '50447afb4728cb2036cf9cc4'
        key: 'retrieve_app_to_evaluate'
        prev_node_id: '50447afb4728cc2036cf9cb3'
        next_node_id: '50447afb4728cc2036cf9cb5'
        name: '15 Days & > 80% Response'
        status: 'IDLE'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '50447afb4728cc2036cf9cc5'
        template_id: '50447afb4728cb2036cf9cc5'
        key: 'retrieve_logo_to_post_and_vote'
        prev_node_id: '50447afb4728cc2036cf9cb4'
        next_node_id: '50447afb4728cc2036cf9cb6'
        name: '10 Days & Submissions >= 3'
        status: 'PASSED'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '50447afb4728cc2036cf9cc6'
        template_id: '50447afb4728cb2036cf9cc6'
        name: '(fallback)'
        key: 'faild_to_evaluate'
        desc: 'Manual trigger back if failed to evaluate'
        prev_node_id: '50447afb4728cc2036cf9cb5'
        next_node_id: '50447afb4728cc2036cf9cb3'
        status: 'IDLE'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '50447afb4728cc2036cf9cc7'
        template_id: '50447afb4728cb2036cf9cc7'
        name: '(fallback)'
        key: 'failed_to_vote'
        desc: 'Manual trigger back if failed to vote'
        prev_node_id: '50447afb4728cc2036cf9cb6'
        next_node_id: '50447afb4728cc2036cf9cb4'
        status: 'IDLE'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '50447afb4728cc2036cf9cc8'
        template_id: '50447afb4728cb2036cf9cc8'
        key: 'evaluate_to_post_final'
        prev_node_id: '50447afb4728cc2036cf9cb5'
        next_node_id: '50447afb4728cc2036cf9cb7'
        status: 'IDLE'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '50447afb4728cc2036cf9cc9'
        template_id: '50447afb4728cb2036cf9cc9'
        key: 'post_and_vote_to_post_final'
        prev_node_id: '50447afb4728cc2036cf9cb6'
        next_node_id: '50447afb4728cc2036cf9cb7'
        status: 'IDLE'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ]
      created_at: new Date(1379528126251)
      updated_at: new Date(1379528126251)
    ,
      id: '50447afb4728cc2036cf9ca1'
      template_id: '50447afb4728cb2036cf9ca1'
      key: 'test_prj'
      name: 'Test Project'
      desc: 'The test project using test workflow'
      status: 'IDLE'
      nodes: [
        id: '507f81413d070321728ffe10'
        template_id: '507f81413d070321728fde10'
        key: 'post_idea'
        name: 'Post Idea'
        desc: 'Post software project ideas'
        workflow_id: '50447afb4728cc2036cf9ca1'
        actions: [
          id: '50447afb4728cc2036cfaca0'
          type: 'SEND_EMAIL'
        ,
          id: '50447afb4728cc2036cfaca1'
          type: 'GENERATE_REPORT'
        ]
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '507f81413d070321728ffe11'
        template_id: '507f81413d070321728fde11'
        key: 'post_cancel'
        name: 'Post Cancel'
        desc: 'Post cancel notification'
        workflow_id: '50447afb4728cc2036cf9ca1'
        actions: []
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '507f81413d070321728ffe12'
        template_id: '507f81413d070321728fde12'
        key: 'post_req'
        name: 'Post Requirement'
        desc: 'Post project requirement'
        workflow_id: '50447afb4728cc2036cf9ca1'
        actions: []
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '507f81413d070321728ffe13'
        template_id: '507f81413d070321728fde13'
        key: 'submit_design'
        name: 'Submit Design'
        desc: 'Retrieve theme design submissions & e-mail to stackholders'
        workflow_id: '50447afb4728cc2036cf9ca1'
        actions: []
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '507f81413d070321728ffe14'
        template_id: '507f81413d070321728fde14'
        key: 'notification'
        name: 'Notification'
        desc: 'Notification'
        workflow_id: '50447afb4728cc2036cf9ca1'
        actions: []
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '507f81413d070321728ffe15'
        template_id: '507f81413d070321728fde15'
        key: 'post_result'
        name: 'Post Result'
        desc: 'Post & e-mail result everyone'
        workflow_id: '50447afb4728cc2036cf9ca1'
        actions: []
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ]
      links: [
        id: '507f81413d070321728ffe22'
        template_id: '507f81413d070321728fde22'
        key: 'post_req_to_submit_design'
        workflow_id: '50447afb4728cc2036cf9ca1'
        prev_node_id: '507f81413d070321728ffe12'
        next_node_id: '507f81413d070321728ffe13'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '507f81413d070321728ffe24'
        template_id: '507f81413d070321728fde24'
        key: 'pass_to_post'
        desc: 'Post & e-mail to everyone if pass rate > 50%'
        workflow_id: '50447afb4728cc2036cf9ca1'
        prev_node_id: '507f81413d070321728ffe13'
        next_node_id: '507f81413d070321728ffe15'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '507f81413d070321728ffe20'
        template_id: '507f81413d070321728fde20'
        key: 'to_cancel'
        name: 'Like < 300'
        desc: 'Cancel if like count < 300'
        workflow_id: '50447afb4728cc2036cf9ca1'
        prev_node_id: '507f81413d070321728ffe10'
        next_node_id: '507f81413d070321728ffe11'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '507f81413d070321728ffe21'
        template_id: '507f81413d070321728fde21'
        key: 'continue_to_req'
        name: 'Like >= 300'
        desc: 'Continue to post requirement if like count >= 300'
        workflow_id: '50447afb4728cc2036cf9ca1'
        prev_node_id: '507f81413d070321728ffe10'
        next_node_id: '507f81413d070321728ffe12'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '507f81413d070321728ffe23'
        template_id: '507f81413d070321728fde23'
        key: 'not_pass_to_notify'
        name: 'Pass rate <= 50%'
        desc: 'Notification if pass rate <= 50%'
        workflow_id: '50447afb4728cc2036cf9ca1'
        prev_node_id: '507f81413d070321728ffe13'
        next_node_id: '507f81413d070321728ffe14'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ]
      created_at: new Date(1379528126251)
      updated_at: new Date(1379528126251)
    ]

    contents: [
      id: 'e57bf4f6aad752f9fc37fcf1'
      title: 'Demo Project Initialization'
      desc: 'Demo Project Initialization Page'
      media: 'PAGE'
      workflow_id: '50447afb4728cc2036cf9ca0'
      node_id: '50447afb4728cc2036cf9cb0'
      action_id: '50447afb4728cc2036cf9f00'
      url: 'about:blank'
      report_id: 'e57bf4f6aad752f9fc51fef0'
      status: 'POSTED'
      created_at: new Date(1371191307656)
      posted_at: new Date(1371191307656)
    ,
      id: 'e57bf4f6aad752f9fc37fcf2'
      title: 'Demo Project Initialization'
      desc: 'Demo Project Initialization Facebook Post'
      media: 'FACEBOOK'
      workflow_id: '50447afb4728cc2036cf9ca0'
      node_id: '50447afb4728cc2036cf9cb0'
      action_id: '50447afb4728cc2036cf9f01'
      url: 'http://facebook.com/'
      report_id: 'e57bf4f6aad752f9fc51fef0'
      status: 'POSTED'
      created_at: new Date(1371191307656)
      posted_at: new Date(1371191307656)
    ,
      id: 'e57bf4f6aad752f9fc37fcf3'
      title: 'Demo Project Initialization'
      desc: 'Demo Project Initialization Twitter Post'
      media: 'TWITTER'
      workflow_id: '50447afb4728cc2036cf9ca0'
      node_id: '50447afb4728cc2036cf9cb0'
      action_id: '50447afb4728cc2036cf9f01'
      report_id: 'e57bf4f6aad752f9fc51fef1'
      url: 'http://twitter.com/'
      status: 'POSTED'
      created_at: new Date(1371191307656)
      posted_at: new Date(1371191307656)
    ,
      id: 'e57bf4f6aad752f9fc37fef1'
      title: 'Demo Project Post Requirements'
      desc: 'Demo Project Post Requirements Page'
      media: 'PAGE'
      workflow_id: '50447afb4728cc2036cf9ca0'
      node_id: '50447afb4728cc2036cf9cb1'
      action_id: '50447afb4728cc2036cf9f03'
      report_id: 'e57bf4f6aad752f9fc51fef1'
      status: 'WAITING'
      created_at: new Date(1371191309656)
    ,
      id: 'e57bf4f6aad752f9fc37fef2'
      title: 'Demo Project Post Requirements'
      desc: 'Demo Project Post Requirements Facebook Post'
      media: 'FACEBOOK'
      workflow_id: '50447afb4728cc2036cf9ca0'
      node_id: '50447afb4728cc2036cf9cb1'
      action_id: '50447afb4728cc2036cf9ce4'
      report_id: 'e57bf4f6aad752f9fc51fef1'
      status: 'WAITING'
      created_at: new Date(1371191309656)
    ,
      id: 'e57bf4f6aad752f9fc37fef3'
      title: 'Demo Project Post Requirements'
      desc: 'Demo Project Post Requirements Twitter Post'
      media: 'TWITTER'
      workflow_id: '50447afb4728cc2036cf9ca0'
      node_id: '50447afb4728cc2036cf9cb1'
      action_id: '50447afb4728cc2036cf9ce4'
      report_id: 'e57bf4f6aad752f9fc51fef1'
      status: 'WAITING'
      created_at: new Date(1371191309656)
    ,
      id: 'e57bf4f6aad752f9fc37feff'
      title: 'Test Content'
      desc: 'Test Content'
      media: 'EMAIL'
      workflow_id: '50447afb4728cc2036cf9ca1'
      node_id: '507f81413d070321728ffe10'
      action_id: '50447afb4728cc2036cfaca0'
      status: 'WAITING'
      created_at: new Date(1371191309656)
    ,
      id: '655a3dd88b20999dd786d38b'
      title: 'CMPE 275 Project Requirements'
      desc: '<p>This is CMPE 275 course project.</p>\n<img src=\'http://upload.wikimedia.org/wikipedia/commons/e/e2/Waterfall_model.svg\'>'
      media: 'PAGE'
      workflow_id: '655a3dd88b20999dd786d38b'
      node_id: 'ec21b6896dd7a39b2e4ae470'
      action_id: 'ec21b6896dd7a39b2e4ae471'
      status: 'WAITING'
      created_at: '2013-06-14T06:28:29.656Z'
      sections: [
        section_title: 'Group Registration'
        section_desc: 'Please enter your group name'
        section_type: 'text'
        text_multiline: false
      ,
        section_title: 'Group Members'
        section_desc: 'Please enter your group members as:\nLast Name, First Name <email> (SID)\ne.g. Young, Wilson <yyfearth@gmail.com> 008060123'
        section_type: 'text'
        text_multiline: true
      ]
    ]

    events: [
      id: 'e57bf4f6aae752e9fc37eef0'
      title: 'Demo Scheduled Event'
      desc: 'Demo scheduled event test data'
      status: 'SCHEDULED'
      type: 'WAIT'
      workflow_id: '50447afb4728cc2036cf9ca1'
      node_id: '507f81413d070321728ffe10'
      action_id: '50447afb4728cc2036cfaca0' # TODO: should be changed
      starts: new Date(1371104900000)
      ends: new Date(1371191300000)
      duration: 86400000 # 1 day
    ,
      id: 'e57bf4f6aae752e9fc37eef1'
      title: 'Demo Unscheduled Event'
      desc: 'Demo unscheduled event test data'
      status: 'UNSCHEDULED'
      type: 'NOWAIT'
      workflow_id: '50447afb4728cc2036cf9ca1'
      node_id: '507f81413d070321728ffe10'
      action_id: '50447afb4728cc2036cfaca0' # TODO: should be changed
      duration: 86400000 # 1 day
    ,
      id: 'e57bf4f6aae752e9fc37eef2'
      title: 'Demo Manual Event'
      desc: 'Demo manual event test data'
      status: 'MANUAL'
      type: 'NOWAIT'
      workflow_id: '50447afb4728cc2036cf9ca1'
      node_id: '507f81413d070321728ffe10'
      action_id: '50447afb4728cc2036cfaca0' # TODO: should be changed
    ]

    reports: [
      id: 'e57bf4f6aad752f9fc51fef0'
      name: 'Demo Project Initialization Report'
      desc: 'Demo Project Initialization Report'
      workflow_id: '50447afb4728cc2036cf9ca0'
      node_id: '50447afb4728cc2036cf9cb0'
      action_id: '50447afb4728cc2036cf9f02'
      status: 'COLLECTED'
      created_at: new Date(1373284304000)
      updated_at: new Date(1373284304000)
      ended_at: new Date(1373457155000)
    ,
      id: 'e57bf4f6aad752f9fc51fef1'
      name: 'Demo Project Post Requirements Report'
      desc: 'Demo Project Post Requirements Report'
      workflow_id: '50447afb4728cc2036cf9ca0'
      node_id: '50447afb4728cc2036cf9cb1'
      action_id: '50447afb4728cc2036cf9f05'
      status: 'COLLECTING'
      created_at: new Date(1373284304000)
      updated_at: new Date(1373284304000)
      ended_at: new Date(1373457155000)
    ]

    notifications: [
      id: 'e57bf4e6aad752f9ec51eee0'
      title: 'Test Notification'
      desc: 'Notification Content'
      type: 'ROUTINE' # ROUTINE/REQUISITE/EMERGENT (imply priority)
      scope: 'PROJECT' # PROJECT/TENANT
      status: 'ACTIVE' # ACTIVE/PROCESSED/EXPIRED
      workflow_id: '50447afb4728cc2036cf9ca1'
      created_at: new Date(1380127473000)
      updated_at: new Date(1380127473000)
      expires_at: new Date(1380213873000)
    ,
      id: 'e57bf4e6aad752f9ec51eee1'
      title: 'Project Start'
      desc: 'Demo Project started at {{date}}'
      workflow_id: '50447afb4728cc2036cf9ca0'
      type: 'ROUTINE'
      scope: 'PROJECT'
      status: 'EXPIRED'
      date: new Date(1379528126251)
      target_url: '#project/50447afb4728cc2036cf9ca0'
      expires_at: new Date(1379614526000)
      created_at: new Date(1379528126000)
      updated_at: new Date(1379614526000)
    ,
      id: 'e57bf4e6aad752f9ec51eee2'
      title: 'Facebook login expired'
      desc: 'The Facebook account binding was expired, please login again from Facebook Connector!'
      type: 'EMERGENT'
      scope: 'TENANT'
      status: 'ACTIVE'
      target_url: '#config/service/facebook'
      created_at: new Date(1379528126000)
      updated_at: new Date(1379614526000)
    ]

  exports = {}
  temp_user = new models.User tenant_id: 0, credential: 'local'
  models.User.current ?= temp_user
  for name, list of data
    name = name.toLowerCase()
    cap = name.charAt(0).toUpperCase() + name[1..]
    Collection = models[cap]
    if Collection
      col = new Collection
      col.create r for r in list
      exports[name] = col
    else
      Model = models[cap[...-1]]
      unless Model
        console.dir models
        throw new Error "unknown test data name #{name} (#{cap})" unless models[cap]
      else for r in list
        model = new Model r
        model.save()
  models.User.current = null if models.User.current is temp_user
  console.log 'test data loaded'
  exports
