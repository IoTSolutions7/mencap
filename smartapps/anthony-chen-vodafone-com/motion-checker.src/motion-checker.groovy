/**
*
* Motion Checker
*
* Copyright Vodafone 2018 Anthony
*
*/

definition(
    name: "Motion Checker",
    namespace: "anthony.chen@vodafone.com",
    author: "Anthony",
    description: "Checks for motion",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/ModeMagic/bon-voyage.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/ModeMagic/bon-voyage%402x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/ModeMagic/bon-voyage%402x.png"
)

preferences {

	section ("Monitor a motion sensor") {
    	input "sensor", "capability.motionSensor", required: true, multiple: false
    }
    
    section ("Define monitoring period") {
    	input "startTime", "time", required: true
        input "endTime", "time", required: true
    }
    
    section ("Detect sustained motion") {
       	input "startTimeLong", "time", required: true
        input "endTimeLong", "time", required: true
        // input "longTime", "time", required: false, title: "Please enter how long you would like to check for sustained motion"
        input "threshold", "number", required: true, title: "Threshold for detections"
    }
    
    section ("Do you want push notifications?") {
    	input "pushMsg", "bool", required: true
    }
    
    section ("Send notifications as Text? (Optional)") {
    	input "phone", "phone", required: false, title: "Please enter a valid number with country code"
    }

}

def installed() {
	initialize()
}

def initialize() {
	log.trace "installed()"
    state.countDetections = 0
	subscribe(sensor, "motion.active", sensorActive)
    subscribe(sensor, "motion.inactive", sensorInactive)
}

def updated() {
	log.trace "updated()"
	unsubscribe()
	initialize()
}


def sensorActive(evt) {
	
    def between = timeOfDayIsBetween(startTime, endTime, new Date(), location.timeZone)
    if (between) {
    	def msgActive = ("Motion detetced at ${sensor.displayName}!")
        if (pushMsg) {
        	sendPush (msgActive)
        }
        if (phone) {
        	sendSms (phone, msgActive)
        }
    } else {
    	pass
    }
    
    def betweenLong = timeOfDayIsBetween(startTimeLong, endTimeLong, new Date(), location.timeZone)
	if (betweenLong) {
    	state.countDetections = state.countDetections + 1
        log.debug "${state.countDetections} and ${threshold}"
        if (state.countDetections >= threshold) {
        	log.debug "test"
            def msgActiveLong = ("Continued motion detetced at ${sensor.displayName}!")
        	sendPush (msgActiveLong)
		}
    }
}

def sensorInactive (evt) {
	log.debug "${sensor.displayName} is not dectecting motion"
}