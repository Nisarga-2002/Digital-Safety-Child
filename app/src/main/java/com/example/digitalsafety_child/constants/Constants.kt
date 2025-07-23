package com.example.digitalsafety_child.constants


object Constants {
    const val NOTIFICATION_CHANNEL_ID = "1001"
    const val LOCATION_UPDATE_INTERVAL_IN_MILLISECONDS = 50
    const val NOTIFICATION_CHANNEL_NAME = "Location_Channel"

    object Location{
        const val Location_Path = "Location"
        const val Location_ID = "location1"
    }

    object SharedPreferences {
        const val IsAuthorized = "isAuthorized"
        const val emailId = "emailId"
    }

    object Intents {
        const val SHARED_PREFERENCES = "SHARED_PREFERENCES"
    }


    object PermissionId {
        const val DEVICE_ADMINISTRATION = 101
        const val OVERLAY = 102
        const val LOCATION = 103
        const val DISPLAY_POP_UP_IN_BACKGROUND = 104
        const val SPECIAL_XIAOMI_PERMISSION = 200
        const val CAMERA = 105
    }

    object ActivityId {
        const val SPLASH_SCREEN = 0
        const val WELCOME_SCREEN = 1
        const val PERMISSION_OVERVIEW = 2
        const val ACTIVATION_SCREEN = 3
        const val SUCCESS_SCREEN = 4
        const val HOME_SCREEN = 5
    }

}