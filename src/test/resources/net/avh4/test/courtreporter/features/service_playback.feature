Feature: Playing back interactions with object graphs

Scenario: Record and play back
  When I record interactions with a service
  And replay the interactions with that service
  Then the replay will have the same result as the original
  And the service will not be called during the replay