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
      password: 'PxdGfIXoXI0ZC+IxNJPz8WbC6Cs7ziltNt2UMhd3Hhk'
      first_name: 'Test'
      last_name: 'User'
      tenant_id: 0
      created_at: '2013-09-05T02:18:13.621Z'
      updated_at: '2013-09-05T02:18:13.621Z'
    ,
      email: 'yyfearth@gmail.com'
      password: 'UGyzwQz5IkbEsRbziurhbwSPCtGj14hmomNxEAU6mkE'
      first_name: 'Wilson'
      last_name: 'Young'
      tenant_id: 0
      created_at: '2013-09-05T02:18:13.621Z'
      updated_at: '2013-09-05T02:18:13.621Z'
    ,
      email: 'otaru14204@hotmail.com'
      password: '0OhYYTpTU3rqawhF7bO93h4BzZ0KffugOwVqhXEGV1A'
      first_name: 'Leo'
      last_name: 'Chu'
      tenant_id: 0
      created_at: '2013-09-05T02:18:13.621Z'
      updated_at: '2013-09-05T02:18:13.621Z'
    ]
    workflows: [
      id: '50447afb4728cb2036cf9ca0'
      title: 'Demo Workflow'
      name: 'demo_workflow'
      desc: 'Demo Workflow for App Dev and Logo Desgin'
      node_ids: [
        '50447afb4728cb2036cf9cb0'
        '50447afb4728cb2036cf9cb1'
        '50447afb4728cb2036cf9cb2'
        '50447afb4728cb2036cf9cb3'
        '50447afb4728cb2036cf9cb4'
        '50447afb4728cb2036cf9cb5'
        '50447afb4728cb2036cf9cb6'
        '50447afb4728cb2036cf9cb7'
      ]
      link_ids: [
        '50447afb4728cb2036cf9cc0'
        '50447afb4728cb2036cf9cc1'
        '50447afb4728cb2036cf9cc2'
        '50447afb4728cb2036cf9cc3'
        '50447afb4728cb2036cf9cc4'
        '50447afb4728cb2036cf9cc5'
        '50447afb4728cb2036cf9cc6'
        '50447afb4728cb2036cf9cc7'
        '50447afb4728cb2036cf9cc8'
        '50447afb4728cb2036cf9cc9'
      ]
      nodes: [
        id: '50447afb4728cb2036cf9cb0'
        title: 'Post Idea'
        name: 'post_idea'
        desc: 'Post project idea'
        created_at: '2013-06-27T00:23:31.747Z'
        updated_at: '2013-06-27T00:23:31.747Z'
        style: 'left: 26px; top: 43px;'
        actions: [
          id: '50447afb4728cb2036cf9f00'
          type: 'post_page'
        ,
          id: '50447afb4728cb2036cf9f01'
          type: 'post_to_multi_social_media'
        ,
          id: '50447afb4728cb2036cf9f02'
          type: 'generate_report'
        ]
      ,
        id: '50447afb4728cb2036cf9cb1'
        title: 'Post Requirements'
        name: 'post_requirements'
        desc: 'Post requirements'
        created_at: '2013-06-27T00:24:55.070Z'
        updated_at: '2013-06-27T00:24:55.070Z'
        style: 'left: 334px; top: 44px;'
        actions: [
          id: '50447afb4728cb2036cf9f03'
          type: 'post_page'
        ,
          id: '50447afb4728cb2036cf9ce4'
          type: 'post_to_multi_social_media'
        ,
          id: '50447afb4728cb2036cf9f05'
          type: 'generate_report'
        ]
      ,
        id: '50447afb4728cb2036cf9cb2'
        title: 'Cancel Notification'
        name: 'cancel_notification'
        desc: 'Cancel project for less response than expected'
        created_at: '2013-06-27T00:25:58.702Z'
        updated_at: '2013-06-27T00:25:58.702Z'
        style: 'left: 334px; top: 204px;'
        actions: [
          id: '50447afb4728cb2036cf9f06'
          type: 'post_to_multi_social_media'
        ]
      ,
        id: '50447afb4728cb2036cf9cb3'
        title: 'Retrieve App Submissions'
        name: 'retrieve_app_submissions'
        desc: 'Retrieve app submissions'
        created_at: '2013-06-27T00:32:46.817Z'
        updated_at: '2013-06-27T00:32:46.817Z'
        style: 'left: 613px; top: 44px;'
        actions: [
          id: '50447afb4728cb2036cf9f07'
          type: 'post_page'
        ,
          id: '50447afb4728cb2036cf9f08'
          type: 'post_to_multi_social_media'
        ,
          id: '50447afb4728cb2036cf9f09'
          type: 'generate_report'
        ]
      ,
        id: '50447afb4728cb2036cf9cb4'
        title: 'Retrieve Logo Design'
        name: 'retrieve_logo_design'
        desc: 'Retrieve logo design'
        created_at: '2013-06-27T00:35:36.856Z'
        updated_at: '2013-06-27T00:35:36.856Z'
        style: 'left: 629px; top: 282px;'
        actions: [
          id: '50447afb4728cb2036cf9f0a'
          type: 'post_page'
        ,
          id: '50447afb4728cb2036cf9f0b'
          type: 'post_to_multi_social_media'
        ,
          id: '50447afb4728cb2036cf9f0c'
          type: 'generate_report'
        ]
      ,
        id: '50447afb4728cb2036cf9cb5'
        title: 'Email to Evaluators'
        name: 'email_to_evaluators'
        desc: 'Email to evaluators'
        created_at: '2013-06-27T00:36:21.950Z'
        updated_at: '2013-06-27T00:36:21.950Z'
        style: 'left: 1018px; top: 44px;'
        actions: [
          id: '50447afb4728cb2036cf9f0d'
          type: 'post_page'
        ,
          id: '50447afb4728cb2036cf9f0e'
          type: 'send_email'
        ,
          id: '50447afb4728cb2036cf9f0f'
          type: 'generate_report'
        ]
      ,
        id: '50447afb4728cb2036cf9cb6'
        title: 'Post and Vote'
        name: 'post_and_vote'
        desc: 'Post and vote'
        created_at: '2013-06-27T00:36:47.097Z'
        updated_at: '2013-06-27T00:36:47.097Z'
        style: 'left: 1043px; top: 282px;'
        actions: [
          id: '50447afb4728cb2036cf9f10'
          type: 'post_page'
        ,
          id: '50447afb4728cb2036cf9f11'
          type: 'post_to_multi_social_media'
        ,
          id: '50447afb4728cb2036cf9f12'
          type: 'generate_report'
        ]
      ,
        id: '50447afb4728cb2036cf9cb7'
        title: 'Post Final Result and Reward'
        name: 'post_final_result_and_reward'
        desc: 'Post final result and reward'
        created_at: '2013-06-27T00:39:04.602Z'
        updated_at: '2013-06-27T00:39:04.602Z'
        style: 'left: 1420px; top: 183px;'
        actions: [
          id: '50447afb4728cb2036cf9f13'
          type: 'post_page'
        ,
          id: '50447afb4728cb2036cf9f14'
          type: 'post_to_multi_social_media'
        ,
          id: '50447afb4728cb2036cf9f15'
          type: 'send_email'
        ,
          id: '50447afb4728cb2036cf9f16'
          type: 'generate_report'
        ]
      ]
      links: [
        id: '50447afb4728cb2036cf9cc0'
        name: '10_days_likes_gte_300'
        prev_node_id: '50447afb4728cb2036cf9cb0'
        next_node_id: '50447afb4728cb2036cf9cb1'
        title: '10 Days & Likes >= 300'
        created_at: '2013-06-27T00:27:31.946Z'
        updated_at: '2013-06-27T00:27:31.946Z'
      ,
        id: '50447afb4728cb2036cf9cc1'
        name: '10_days_likes_lt_300'
        prev_node_id: '50447afb4728cb2036cf9cb0'
        next_node_id: '50447afb4728cb2036cf9cb2'
        title: '10 Days & Likes < 300'
        created_at: '2013-06-27T00:28:58.636Z'
        updated_at: '2013-06-27T00:28:58.636Z'
      ,
        id: '50447afb4728cb2036cf9cc2'
        name: 'post_req_to_retrieve_app'
        prev_node_id: '50447afb4728cb2036cf9cb1'
        next_node_id: '50447afb4728cb2036cf9cb3'
        created_at: '2013-06-27T00:33:01.670Z'
        updated_at: '2013-06-27T00:33:01.670Z'
      ,
        id: '50447afb4728cb2036cf9cc3'
        name: 'post_req_to_retrieve_logo'
        prev_node_id: '50447afb4728cb2036cf9cb1'
        next_node_id: '50447afb4728cb2036cf9cb4'
        created_at: '2013-06-27T00:35:45.912Z'
        updated_at: '2013-06-27T00:35:45.912Z'
      ,
        id: '50447afb4728cb2036cf9cc4'
        name: 'retrieve_app_to_evaluate'
        prev_node_id: '50447afb4728cb2036cf9cb3'
        next_node_id: '50447afb4728cb2036cf9cb5'
        title: '15 Days & > 80% Response'
        created_at: '2013-06-27T00:37:24.163Z'
        updated_at: '2013-06-27T00:37:24.163Z'
      ,
        id: '50447afb4728cb2036cf9cc5'
        name: 'retrieve_logo_to_post_and_vote'
        prev_node_id: '50447afb4728cb2036cf9cb4'
        next_node_id: '50447afb4728cb2036cf9cb6'
        title: '10 Days & Submissions >= 3'
        created_at: '2013-06-27T00:37:43.706Z'
        updated_at: '2013-06-27T00:37:43.706Z'
      ,
        id: '50447afb4728cb2036cf9cc6'
        title: '(fallback)'
        name: 'faild_to_evaluate'
        desc: 'Manual trigger back if failed to evaluate'
        prev_node_id: '50447afb4728cb2036cf9cb5'
        next_node_id: '50447afb4728cb2036cf9cb3'
        created_at: '2013-06-27T00:37:51.943Z'
        updated_at: '2013-06-27T00:37:51.943Z'
      ,
        id: '50447afb4728cb2036cf9cc7'
        title: '(fallback)'
        name: 'failed_to_vote'
        desc: 'Manual trigger back if failed to vote'
        prev_node_id: '50447afb4728cb2036cf9cb6'
        next_node_id: '50447afb4728cb2036cf9cb4'
        created_at: '2013-06-27T00:38:05.360Z'
        updated_at: '2013-06-27T00:38:05.360Z'
      ,
        id: '50447afb4728cb2036cf9cc8'
        name: 'evaluate_to_post_final'
        prev_node_id: '50447afb4728cb2036cf9cb5'
        next_node_id: '50447afb4728cb2036cf9cb7'
        created_at: '2013-06-27T00:39:22.841Z'
        updated_at: '2013-06-27T00:39:22.841Z'
      ,
        id: '50447afb4728cb2036cf9cc9'
        name: 'post_and_vote_to_post_final'
        prev_node_id: '50447afb4728cb2036cf9cb6'
        next_node_id: '50447afb4728cb2036cf9cb7'
        created_at: '2013-06-27T00:39:26.674Z'
        updated_at: '2013-06-27T00:39:26.674Z'
      ]
      created_at: '2013-06-27T00:22:20.272Z'
      updated_at: '2013-06-27T00:22:20.272Z'
    ,
      id: '50447afb4728cb2036cf9ca1'
      name: 'test_wf'
      title: 'Test Workflow'
      desc: 'The test workflow'
      created_at: new Date(1363879373649)
      updated_at: new Date(1363879373649)
      nodes: [
        id: '507f81413d070321728fde10'
        name: 'post_idea'
        title: 'Post Idea'
        desc: 'Post software project ideas'
        workflow_id: '50447afb4728cb2036cf9ca1'
        actions: []
        created_at: new Date(1363879373649)
        updated_at: new Date(1363879373649)
      ,
        id: '507f81413d070321728fde11'
        name: 'post_cancel'
        title: 'Post Cancel'
        desc: 'Post cancel notification'
        workflow_id: '50447afb4728cb2036cf9ca1'
        actions: []
        created_at: new Date(1363879373649)
        updated_at: new Date(1363879373649)
      ,
        id: '507f81413d070321728fde12'
        name: 'post_req'
        title: 'Post Requirement'
        desc: 'Post project requirement'
        workflow_id: '50447afb4728cb2036cf9ca1'
        actions: []
        created_at: new Date(1363879373649)
        updated_at: new Date(1363879373649)
      ,
        id: '507f81413d070321728fde13'
        name: 'submit_design'
        title: 'Submit Design'
        desc: 'Retrieve theme design submissions & e-mail to stackholders'
        workflow_id: '50447afb4728cb2036cf9ca1'
        actions: []
        created_at: new Date(1363879373649)
        updated_at: new Date(1363879373649)
      ,
        id: '507f81413d070321728fde14'
        name: 'notification'
        title: 'Notification'
        desc: 'Notification'
        workflow_id: '50447afb4728cb2036cf9ca1'
        actions: []
        created_at: new Date(1363879373649)
        updated_at: new Date(1363879373649)
      ,
        id: '507f81413d070321728fde15'
        name: 'post_result'
        title: 'Post Result'
        desc: 'Post & e-mail result everyone'
        workflow_id: '50447afb4728cb2036cf9ca1'
        actions: []
        created_at: new Date(1363879373649)
        updated_at: new Date(1363879373649)
      ]
      links: [
        id: '507f81413d070321728fde22'
        name: 'post_req_to_submit_design'
        workflow_id: '50447afb4728cb2036cf9ca1'
        prev_node_id: '507f81413d070321728fde12'
        next_node_id: '507f81413d070321728fde13'
        created_at: new Date(1363879373649)
        updated_at: new Date(1363879373649)
      ,
        id: '507f81413d070321728fde24'
        name: 'pass_to_post'
        desc: 'Post & e-mail to everyone if pass rate > 50%'
        workflow_id: '50447afb4728cb2036cf9ca1'
        prev_node_id: '507f81413d070321728fde13'
        next_node_id: '507f81413d070321728fde15'
        created_at: new Date(1363879373649)
        updated_at: new Date(1363879373649)
      ,
        id: '507f81413d070321728fde20'
        name: 'to_cancel'
        title: 'Like < 300'
        desc: 'Cancel if like count < 300'
        workflow_id: '50447afb4728cb2036cf9ca1'
        prev_node_id: '507f81413d070321728fde10'
        next_node_id: '507f81413d070321728fde11'
        created_at: new Date(1363879373649)
        updated_at: new Date(1363879373649)
      ,
        id: '507f81413d070321728fde21'
        name: 'continue_to_req'
        title: 'Like >= 300'
        desc: 'Continue to post requirement if like count >= 300'
        workflow_id: '50447afb4728cb2036cf9ca1'
        prev_node_id: '507f81413d070321728fde10'
        next_node_id: '507f81413d070321728fde12'
        created_at: new Date(1363879402429)
        updated_at: new Date(1363879402429)
      ,
        id: '507f81413d070321728fde23'
        name: 'not_pass_to_notify'
        title: 'Pass rate <= 50%'
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
      workflow_id: '50447afb4728cb2036cf9ca0'
      title: 'Demo Project'
      name: 'demo_prj'
      desc: 'Demo Project from Demo Workflow'
      status: 'RUNNING'
      node_ids: [
        '50447afb4728cc2036cf9cb0'
        '50447afb4728cc2036cf9cb1'
        '50447afb4728cc2036cf9cb2'
        '50447afb4728cc2036cf9cb3'
        '50447afb4728cc2036cf9cb4'
        '50447afb4728cc2036cf9cb5'
        '50447afb4728cc2036cf9cb6'
        '50447afb4728cc2036cf9cb7'
      ]
      link_ids: [
        '50447afb4728cc2036cf9cc0'
        '50447afb4728cc2036cf9cc1'
        '50447afb4728cc2036cf9cc2'
        '50447afb4728cc2036cf9cc3'
        '50447afb4728cc2036cf9cc4'
        '50447afb4728cc2036cf9cc5'
        '50447afb4728cc2036cf9cc6'
        '50447afb4728cc2036cf9cc7'
        '50447afb4728cc2036cf9cc8'
        '50447afb4728cc2036cf9cc9'
      ]
      nodes: [
        id: '50447afb4728cc2036cf9cb0'
        template_id: '50447afb4728cb2036cf9cb0'
        title: 'Post Idea'
        name: 'post_idea'
        desc: 'Post project idea'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
        style: 'left: 26px; top: 43px;'
        actions: [
          id: '50447afb4728cc2036cf9f00'
          type: 'post_page'
        ,
          id: '50447afb4728cc2036cf9f01'
          type: 'post_to_multi_social_media'
        ,
          id: '50447afb4728cc2036cf9f02'
          type: 'generate_report'
        ]
      ,
        id: '50447afb4728cc2036cf9cb1'
        template_id: '50447afb4728cb2036cf9cb1'
        title: 'Post Requirements'
        name: 'post_requirements'
        desc: 'Post requirements'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
        style: 'left: 334px; top: 44px;'
        actions: [
          id: '50447afb4728cc2036cf9f03'
          type: 'post_page'
        ,
          id: '50447afb4728cc2036cf9ce4'
          type: 'post_to_multi_social_media'
        ,
          id: '50447afb4728cc2036cf9f05'
          type: 'generate_report'
        ]
      ,
        id: '50447afb4728cc2036cf9cb2'
        template_id: '50447afb4728cb2036cf9cb2'
        title: 'Cancel Notification'
        name: 'cancel_notification'
        desc: 'Cancel project for less response than expected'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
        style: 'left: 334px; top: 204px;'
        actions: [
          id: '50447afb4728cc2036cf9f06'
          type: 'post_to_multi_social_media'
        ]
      ,
        id: '50447afb4728cc2036cf9cb3'
        template_id: '50447afb4728cb2036cf9cb3'
        title: 'Retrieve App Submissions'
        name: 'retrieve_app_submissions'
        desc: 'Retrieve app submissions'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
        style: 'left: 613px; top: 44px;'
        actions: [
          id: '50447afb4728cc2036cf9f07'
          type: 'post_page'
        ,
          id: '50447afb4728cc2036cf9f08'
          type: 'post_to_multi_social_media'
        ,
          id: '50447afb4728cc2036cf9f09'
          type: 'generate_report'
        ]
      ,
        id: '50447afb4728cc2036cf9cb4'
        template_id: '50447afb4728cb2036cf9cb4'
        title: 'Retrieve Logo Design'
        name: 'retrieve_logo_design'
        desc: 'Retrieve logo design'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
        style: 'left: 629px; top: 282px;'
        actions: [
          id: '50447afb4728cc2036cf9f0a'
          type: 'post_page'
        ,
          id: '50447afb4728cc2036cf9f0b'
          type: 'post_to_multi_social_media'
        ,
          id: '50447afb4728cc2036cf9f0c'
          type: 'generate_report'
        ]
      ,
        id: '50447afb4728cc2036cf9cb5'
        template_id: '50447afb4728cb2036cf9cb5'
        title: 'Email to Evaluators'
        name: 'email_to_evaluators'
        desc: 'Email to evaluators'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
        style: 'left: 1018px; top: 44px;'
        actions: [
          id: '50447afb4728cc2036cf9f0d'
          type: 'post_page'
        ,
          id: '50447afb4728cc2036cf9f0e'
          type: 'send_email'
        ,
          id: '50447afb4728cc2036cf9f0f'
          type: 'generate_report'
        ]
      ,
        id: '50447afb4728cc2036cf9cb6'
        template_id: '50447afb4728cb2036cf9cb6'
        title: 'Post and Vote'
        name: 'post_and_vote'
        desc: 'Post and vote'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
        style: 'left: 1043px; top: 282px;'
        actions: [
          id: '50447afb4728cc2036cf9f10'
          type: 'post_page'
        ,
          id: '50447afb4728cc2036cf9f11'
          type: 'post_to_multi_social_media'
        ,
          id: '50447afb4728cc2036cf9f12'
          type: 'generate_report'
        ]
      ,
        id: '50447afb4728cc2036cf9cb7'
        template_id: '50447afb4728cb2036cf9cb7'
        title: 'Post Final Result and Reward'
        name: 'post_final_result_and_reward'
        desc: 'Post final result and reward'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
        style: 'left: 1420px; top: 183px;'
        actions: [
          id: '50447afb4728cc2036cf9f13'
          type: 'post_page'
        ,
          id: '50447afb4728cc2036cf9f14'
          type: 'post_to_multi_social_media'
        ,
          id: '50447afb4728cc2036cf9f15'
          type: 'send_email'
        ,
          id: '50447afb4728cc2036cf9f16'
          type: 'generate_report'
        ]
      ]
      links: [
        id: '50447afb4728cc2036cf9cc0'
        template_id: '50447afb4728cb2036cf9cc0'
        name: '10_days_likes_gte_300'
        prev_node_id: '50447afb4728cc2036cf9cb0'
        next_node_id: '50447afb4728cc2036cf9cb1'
        title: '10 Days & Likes >= 300'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '50447afb4728cc2036cf9cc1'
        template_id: '50447afb4728cb2036cf9cc1'
        name: '10_days_likes_lt_300'
        prev_node_id: '50447afb4728cc2036cf9cb0'
        next_node_id: '50447afb4728cc2036cf9cb2'
        title: '10 Days & Likes < 300'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '50447afb4728cc2036cf9cc2'
        template_id: '50447afb4728cb2036cf9cc2'
        name: 'post_req_to_retrieve_app'
        prev_node_id: '50447afb4728cc2036cf9cb1'
        next_node_id: '50447afb4728cc2036cf9cb3'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '50447afb4728cc2036cf9cc3'
        template_id: '50447afb4728cb2036cf9cc3'
        name: 'post_req_to_retrieve_logo'
        prev_node_id: '50447afb4728cc2036cf9cb1'
        next_node_id: '50447afb4728cc2036cf9cb4'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '50447afb4728cc2036cf9cc4'
        template_id: '50447afb4728cb2036cf9cc4'
        name: 'retrieve_app_to_evaluate'
        prev_node_id: '50447afb4728cc2036cf9cb3'
        next_node_id: '50447afb4728cc2036cf9cb5'
        title: '15 Days & > 80% Response'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '50447afb4728cc2036cf9cc5'
        template_id: '50447afb4728cb2036cf9cc5'
        name: 'retrieve_logo_to_post_and_vote'
        prev_node_id: '50447afb4728cc2036cf9cb4'
        next_node_id: '50447afb4728cc2036cf9cb6'
        title: '10 Days & Submissions >= 3'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '50447afb4728cc2036cf9cc6'
        template_id: '50447afb4728cb2036cf9cc6'
        title: '(fallback)'
        name: 'faild_to_evaluate'
        desc: 'Manual trigger back if failed to evaluate'
        prev_node_id: '50447afb4728cc2036cf9cb5'
        next_node_id: '50447afb4728cc2036cf9cb3'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '50447afb4728cc2036cf9cc7'
        template_id: '50447afb4728cb2036cf9cc7'
        title: '(fallback)'
        name: 'failed_to_vote'
        desc: 'Manual trigger back if failed to vote'
        prev_node_id: '50447afb4728cc2036cf9cb6'
        next_node_id: '50447afb4728cc2036cf9cb4'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '50447afb4728cc2036cf9cc8'
        template_id: '50447afb4728cb2036cf9cc8'
        name: 'evaluate_to_post_final'
        prev_node_id: '50447afb4728cc2036cf9cb5'
        next_node_id: '50447afb4728cc2036cf9cb7'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '50447afb4728cc2036cf9cc9'
        template_id: '50447afb4728cb2036cf9cc9'
        name: 'post_and_vote_to_post_final'
        prev_node_id: '50447afb4728cc2036cf9cb6'
        next_node_id: '50447afb4728cc2036cf9cb7'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ]
      created_at: new Date(1379528126251)
      updated_at: new Date(1379528126251)
    ,
      id: '50447afb4728cc2036cf9ca1'
      workflow_id: '50447afb4728cb2036cf9ca1'
      name: 'test_prj'
      title: 'Test Project'
      desc: 'The test project using test workflow'
      status: 'WAITING'
      nodes: [
        id: '507f81413d070321728ffe10'
        template_id: '507f81413d070321728fde10'
        name: 'post_idea'
        title: 'Post Idea'
        desc: 'Post software project ideas'
        workflow_id: '50447afb4728cc2036cf9ca1'
        actions: [
          id: '50447afb4728cc2036cfaca0'
          type: 'send_email'
        ,
          id: '50447afb4728cc2036cfaca1'
          type: 'generate_report'
        ]
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '507f81413d070321728ffe11'
        template_id: '507f81413d070321728fde11'
        name: 'post_cancel'
        title: 'Post Cancel'
        desc: 'Post cancel notification'
        workflow_id: '50447afb4728cc2036cf9ca1'
        actions: []
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '507f81413d070321728ffe12'
        template_id: '507f81413d070321728fde12'
        name: 'post_req'
        title: 'Post Requirement'
        desc: 'Post project requirement'
        workflow_id: '50447afb4728cc2036cf9ca1'
        actions: []
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '507f81413d070321728ffe13'
        template_id: '507f81413d070321728fde13'
        name: 'submit_design'
        title: 'Submit Design'
        desc: 'Retrieve theme design submissions & e-mail to stackholders'
        workflow_id: '50447afb4728cc2036cf9ca1'
        actions: []
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '507f81413d070321728ffe14'
        template_id: '507f81413d070321728fde14'
        name: 'notification'
        title: 'Notification'
        desc: 'Notification'
        workflow_id: '50447afb4728cc2036cf9ca1'
        actions: []
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '507f81413d070321728ffe15'
        template_id: '507f81413d070321728fde15'
        name: 'post_result'
        title: 'Post Result'
        desc: 'Post & e-mail result everyone'
        workflow_id: '50447afb4728cc2036cf9ca1'
        actions: []
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ]
      links: [
        id: '507f81413d070321728ffe22'
        template_id: '507f81413d070321728fde22'
        name: 'post_req_to_submit_design'
        workflow_id: '50447afb4728cc2036cf9ca1'
        prev_node_id: '507f81413d070321728ffe12'
        next_node_id: '507f81413d070321728ffe13'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '507f81413d070321728ffe24'
        template_id: '507f81413d070321728fde24'
        name: 'pass_to_post'
        desc: 'Post & e-mail to everyone if pass rate > 50%'
        workflow_id: '50447afb4728cc2036cf9ca1'
        prev_node_id: '507f81413d070321728ffe13'
        next_node_id: '507f81413d070321728ffe15'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '507f81413d070321728ffe20'
        template_id: '507f81413d070321728fde20'
        name: 'to_cancel'
        title: 'Like < 300'
        desc: 'Cancel if like count < 300'
        workflow_id: '50447afb4728cc2036cf9ca1'
        prev_node_id: '507f81413d070321728ffe10'
        next_node_id: '507f81413d070321728ffe11'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '507f81413d070321728ffe21'
        template_id: '507f81413d070321728fde21'
        name: 'continue_to_req'
        title: 'Like >= 300'
        desc: 'Continue to post requirement if like count >= 300'
        workflow_id: '50447afb4728cc2036cf9ca1'
        prev_node_id: '507f81413d070321728ffe10'
        next_node_id: '507f81413d070321728ffe12'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ,
        id: '507f81413d070321728ffe23'
        template_id: '507f81413d070321728fde23'
        name: 'not_pass_to_notify'
        title: 'Pass rate <= 50%'
        desc: 'Notification if pass rate <= 50%'
        workflow_id: '50447afb4728cc2036cf9ca1'
        prev_node_id: '507f81413d070321728ffe13'
        next_node_id: '507f81413d070321728ffe14'
        created_at: new Date(1379528126251)
        updated_at: new Date(1379528126251)
      ]
      node_ids: [
        '507f81413d070321728ffe10'
        '507f81413d070321728ffe11'
        '507f81413d070321728ffe12'
        '507f81413d070321728ffe13'
        '507f81413d070321728ffe14'
        '507f81413d070321728ffe15'
      ]
      link_ids: [
        '507f81413d070321728ffe20'
        '507f81413d070321728ffe21'
        '507f81413d070321728ffe22'
        '507f81413d070321728ffe23'
        '507f81413d070321728ffe24'
      ]
      created_at: new Date(1379528126251)
      updated_at: new Date(1379528126251)
    ]

    contents: [
      id: 'e57bf4f6aad752f9fc37fcf1'
      title: 'Demo Project Initialization'
      desc: 'Demo Project Initialization Page'
      media: 'PAGE'
      project_id: '50447afb4728cc2036cf9ca0'
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
      project_id: '50447afb4728cc2036cf9ca0'
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
      project_id: '50447afb4728cc2036cf9ca0'
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
      project_id: '50447afb4728cc2036cf9ca0'
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
      project_id: '50447afb4728cc2036cf9ca0'
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
      project_id: '50447afb4728cc2036cf9ca0'
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
      project_id: '50447afb4728cc2036cf9ca1'
      node_id: '507f81413d070321728ffe10'
      action_id: '50447afb4728cc2036cfaca0'
      status: 'WAITING'
      created_at: new Date(1371191309656)
    ]

    events: [
      id: 'e57bf4f6aae752e9fc37eef0'
      title: 'Demo Scheduled Event'
      desc: 'Demo scheduled event test data'
      status: 'SCHEDULED'
      type: 'WAIT'
      project_id: '50447afb4728cc2036cf9ca1'
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
      project_id: '50447afb4728cc2036cf9ca1'
      node_id: '507f81413d070321728ffe10'
      action_id: '50447afb4728cc2036cfaca0' # TODO: should be changed
      duration: 86400000 # 1 day
    ,
      id: 'e57bf4f6aae752e9fc37eef2'
      title: 'Demo Manual Event'
      desc: 'Demo manual event test data'
      status: 'MANUAL'
      type: 'NOWAIT'
      project_id: '50447afb4728cc2036cf9ca1'
      node_id: '507f81413d070321728ffe10'
      action_id: '50447afb4728cc2036cfaca0' # TODO: should be changed
    ]

    reports: [
      id: 'e57bf4f6aad752f9fc51fef0'
      title: 'Demo Project Initialization Report'
      desc: 'Demo Project Initialization Report'
      project_id: '50447afb4728cc2036cf9ca0'
      node_id: '50447afb4728cc2036cf9cb0'
      action_id: '50447afb4728cc2036cf9f02'
      status: 'COLLECTED'
      created_at: new Date(1373284304000)
      updated_at: new Date(1373284304000)
      ended_at: new Date(1373457155000)
    ,
      id: 'e57bf4f6aad752f9fc51fef1'
      title: 'Demo Project Post Requirements Report'
      desc: 'Demo Project Post Requirements Report'
      project_id: '50447afb4728cc2036cf9ca0'
      node_id: '50447afb4728cc2036cf9cb1'
      action_id: '50447afb4728cc2036cf9f05'
      status: 'COLLECTING'
      created_at: new Date(1373284304000)
      updated_at: new Date(1373284304000)
      ended_at: new Date(1373457155000)
    ]

  exports = {}
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
        throw "unknown test data name #{name} (#{cap})" unless models[cap]
      else for r in list
        model = new Model r
        model.save()
  console.log 'test data loaded'
  exports
