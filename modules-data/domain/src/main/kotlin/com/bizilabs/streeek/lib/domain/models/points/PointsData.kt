package com.bizilabs.streeek.lib.domain.models.points

internal fun getAllPoints(): List<EventPointsDomain> {
    return listOf(
        EventPointsDomain.Single(
            title = "CommitCommentEvent",
            subtitle = "A commit comment is created",
            description = "You receive 5 points when you create a commit comment ",
            points = 5,
        ),
        EventPointsDomain.Multiple(
            title = "CreateEvent",
            subtitle = "A Git branch or tag is created",
            description = "You receive different points for different actions. Also we ensure that the pusher is not a bot but a user",
            actions =
                listOf(
                    EventPointsDomain.Multiple.MultiplePoint(
                        action = "repository",
                        points = 15,
                        description = "You get 15 points when you create a new repository",
                    ),
                    EventPointsDomain.Multiple.MultiplePoint(
                        action = "tag",
                        points = 10,
                        description = "you get 10 points when you create a new tag",
                    ),
                    EventPointsDomain.Multiple.MultiplePoint(
                        action = "branch",
                        points = 5,
                        description = "you get 5 points when you create a new branch",
                    ),
                ),
        ),
        EventPointsDomain.Multiple(
            title = "DeleteEvent",
            subtitle = "A Git branch or tag is deleted",
            description = "You receive minus points for any delete action",
            actions =
                listOf(
                    EventPointsDomain.Multiple.MultiplePoint(
                        action = "branch",
                        points = -5,
                        description = "you get -5 points when you delete a branch",
                    ),
                    EventPointsDomain.Multiple.MultiplePoint(
                        action = "tag",
                        points = -10,
                        description = "you get -10 points when you delete a tag",
                    ),
                ),
        ),
        EventPointsDomain.Single(
            title = "ForkEvent",
            subtitle = "A user forks a repository",
            description = "you get 15 points when you fork any repository",
            points = 15,
        ),
        EventPointsDomain.Single(
            title = "GollumEvent",
            subtitle = "A wiki page is created or updated",
            description = "you receive 20 points when a wiki page is created or updated",
            points = 20,
        ),
        EventPointsDomain.Single(
            title = "IssueCommentEvent",
            subtitle = "Activity related to an issue or pull request comment",
            description = "you get 5 points if when an issue comment is created or updated",
            points = 5,
        ),
        EventPointsDomain.Multiple(
            title = "IssuesEvent",
            subtitle = "Activity related to an issue",
            description = "you receive different points when in case of any action to an issue",
            actions =
                listOf(
                    EventPointsDomain.Multiple.MultiplePoint(
                        action = "opened",
                        points = 30,
                        description = "points for creating an issue",
                    ),
                    EventPointsDomain.Multiple.MultiplePoint(
                        action = "edited",
                        points = 10,
                        description = "points for editing an issue",
                    ),
                    EventPointsDomain.Multiple.MultiplePoint(
                        action = "closed",
                        points = 30,
                        description = "points for closing an issue",
                    ),
                ),
        ),
        EventPointsDomain.Single(
            title = "MemberEvent",
            subtitle = "Activity related to repository collaborators",
            description = "points for adding a new member",
            points = 20,
        ),
        EventPointsDomain.Single(
            title = "PublicEvent",
            subtitle = "When a private repository is made public",
            description = "points for making a repository public",
            points = 15,
        ),
        EventPointsDomain.Multiple(
            title = "PullRequestEvent",
            subtitle = "Activity related to pull requests",
            description = "you receive different points when in case of any action to a pull request",
            actions =
                listOf(
                    EventPointsDomain.Multiple.MultiplePoint(
                        action = "closed",
                        points = 50,
                        description = "points for closing a pull request",
                    ),
                    EventPointsDomain.Multiple.MultiplePoint(
                        action = "opened",
                        points = 40,
                        description = "points for opening a PR",
                    ),
                    EventPointsDomain.Multiple.MultiplePoint(
                        action = "reopened",
                        points = 30,
                        description = "points for reopening a PR",
                    ),
                    EventPointsDomain.Multiple.MultiplePoint(
                        action = "edited",
                        points = 10,
                        description = "points for editing a PR",
                    ),
                    EventPointsDomain.Multiple.MultiplePoint(
                        action = "labeled",
                        points = 5,
                        description = "points for labelling an issue",
                    ),
                    EventPointsDomain.Multiple.MultiplePoint(
                        action = "others",
                        points = 5,
                        description = "points for any other action on a PR",
                    ),
                ),
        ),
        EventPointsDomain.Single(
            title = "PullRequestReviewEvent",
            subtitle = "Activity related to pull request reviews",
            description = "points for reviewing a PR",
            points = 12,
        ),
        EventPointsDomain.Multiple(
            title = "PullRequestReviewCommentEvent",
            subtitle = "Activity related to pull request review comments in the pull request's unified diff",
            description = "points for adding or editing a comment on a PR",
            actions =
                listOf(
                    EventPointsDomain.Multiple.MultiplePoint(
                        action = "created",
                        points = 15,
                        description = "points for creating a review comment",
                    ),
                    EventPointsDomain.Multiple.MultiplePoint(
                        action = "edited",
                        points = 5,
                        description = "points for editing a review comment",
                    ),
                    EventPointsDomain.Multiple.MultiplePoint(
                        action = "other",
                        points = 5,
                        description = "points for any other action to a review comment",
                    ),
                ),
        ),
        EventPointsDomain.Multiple(
            title = "PullRequestReviewThreadEvent",
            subtitle = "Activity related to a comment thread on a pull request being marked as resolved or unresolved",
            description = "points for any related action for a review thread",
            actions =
                listOf(
                    EventPointsDomain.Multiple.MultiplePoint(
                        action = "resolved",
                        points = 25,
                        description = "points for resolving a review thread",
                    ),
                    EventPointsDomain.Multiple.MultiplePoint(
                        action = "unresolved",
                        points = -30,
                        description = "points for unresolving a review thread",
                    ),
                    EventPointsDomain.Multiple.MultiplePoint(
                        action = "other",
                        points = 0,
                        description = "no points for any other action",
                    ),
                ),
        ),
        EventPointsDomain.Single(
            title = "PushEvent",
            subtitle = "One or more commits are pushed to a repository branch or tag",
            description = "points for pushing commits for each commit",
            points = 5,
        ),
        EventPointsDomain.Single(
            title = "ReleaseEvent",
            subtitle = "Activity related to a release",
            description = "points for creating a new release",
            points = 100,
        ),
        EventPointsDomain.Single(
            title = "SponsorshipEvent",
            subtitle = "Activity related to a sponsorship listing",
            description = "points for when a new sponsor is added",
            points = 150,
        ),
        EventPointsDomain.Single(
            title = "WatchEvent",
            subtitle = "When someone stars a repository",
            description = "points for starting a repository",
            points = 5,
        ),
    ).sortedBy { it.title.lowercase() }
}
