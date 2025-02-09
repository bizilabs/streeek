package com.bizilabs.streeek.lib.domain.models.notifications

data class NotificationMessage(
    val title: String,
    val body: String,
)

val DroppedPositionMessage =
    listOf(
        NotificationMessage(
            title = "Oops! 😟 You've been outpaced in {team_name}!",
            body = "Dropped a few spots. Time to hustle and climb back!",
        ),
        NotificationMessage(
            title = "Looks like gravity got you in {team_name}! 🔻",
            body = "You've slipped down the ladder. Bounce back soon!",
        ),
        NotificationMessage(
            title = "Yikes! 😬 Someone in {team_name} has been practicing!",
            body = "You’ve slid down a spot. Show them what you’ve got!",
        ),
        NotificationMessage(
            title = "Oopsie-daisy 🙈 in {team_name}!",
            body = "Your rank took a tumble. Time for a comeback!",
        ),
        NotificationMessage(
            title = "The competition in {team_name} is fierce! 💪",
            body = "You've dropped a spot. Time to turn the heat up!",
        ),
        NotificationMessage(
            title = "Whoops! 🤦 You've taken a dive in {team_name}!",
            body = "Time to rise and shine again!",
        ),
        NotificationMessage(
            title = "Slippery slope ⚠️ alert in {team_name}!",
            body = "You’ve fallen back a step. Show them who's boss!",
        ),
        NotificationMessage(
            title = "Your rank 🕺 is doing the cha-cha in {team_name}!",
            body = "Shake things up and reclaim it!",
        ),
        NotificationMessage(
            title = "Alert 📢 from {team_name} leaderboard!",
            body = "You've been bumped down the ranks. Time to suit up and stage an epic comeback!",
        ),
        NotificationMessage(
            title = "Well, well 🤔, someone in {team_name} got ahead of you!",
            body = "Plot your revenge and climb back up!",
        ),
    )

val ClimbedPositionMessage =
    listOf(
        NotificationMessage(
            title = "🚀 Skyrocketing in {team_name}!",
            body = "You’re climbing faster than a cat up a tree. Keep scratching your way to the top!",
        ),
        NotificationMessage(
            title = "🔥 You’re on fire in {team_name}!",
            body = "Your rank just went up in smoke—straight to the top!",
        ),
        NotificationMessage(
            title = "🏆 Ladder boss in {team_name}!",
            body = "You’re climbing so fast they’ll need binoculars to catch up!",
        ),
        NotificationMessage(
            title = "💥 Whoa! Big moves in {team_name}!",
            body = "Your rank just did a parkour flip upward. Keep the tricks coming!",
        ),
        NotificationMessage(
            title = "🚀 Rocket-powered in {team_name}!",
            body = "You’re zooming up the leaderboard. Who needs gravity anyway?",
        ),
        NotificationMessage(
            title = "👑 Stepping on heads in {team_name}!",
            body = "You’ve climbed over a few spots. Don’t forget to wave from the top!",
        ),
        NotificationMessage(
            title = "⚡ From zero to hero in {team_name}!",
            body = "Your position just went turbo mode. Keep the engines roaring!",
        ),
        NotificationMessage(
            title = "👑 King/Queen of the hill in {team_name}!",
            body = "You’re owning that leaderboard climb. Crown yourself already!",
        ),
        NotificationMessage(
            title = "💥 Breaking ranks in {team_name}!",
            body = "You’re moving up like a ninja in the night. Keep it stealthy and strong!",
        ),
        NotificationMessage(
            title = "🎶 Leaderboard legend in {team_name}!",
            body = "Your climb is legendary. Soon, they’ll write songs about you!",
        ),
    )

val LevelledUpMessages =
    listOf(
        NotificationMessage(
            title = "🌟 Level Up! Welcome to {level_name} 🌟",
            body = "You're unstoppable! Keep those contributions coming and dominate the leaderboard. 🚀",
        ),
        NotificationMessage(
            title = "🎉 Boom! You've Hit {level_name}! 🎉",
            body = "Your coding streak is on fire! 🔥 Let's see how high you can climb. 💪",
        ),
        NotificationMessage(
            title = "⚡ Leveling Up to {level_name}! ⚡",
            body = "Your skills are leveling up faster than a speedrun! Keep crushing it! 💥",
        ),
        NotificationMessage(
            title = "🏆 New Level Unlocked: {level_name}! 🏆",
            body = "Congratulations! Your contributions are now legendary. Go claim that throne! 👑",
        ),
        NotificationMessage(
            title = "🎮 Next Level: {level_name} Unlocked! 🎮",
            body = "You’re in the big leagues now! Ready for your next coding quest? 💻✨",
        ),
    )
