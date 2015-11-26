/**
 *  Double Duty 
 *
 *  Copyright 2015 Pasquale Ranalli
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "Double Duty",
    namespace: "pranalli",
    author: "Pasquale Ranalli",
    description: "This app allows you to use redundant \"off\" and/or \"on\" switch presses to control secondary lights.  You paid a lot for those switches, make them work double duty!",  
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet@2x.png"
)

preferences {
	page(name: "selectPrefs")
   	}
    
def selectPrefs() {
	dynamicPage(name: "selectPrefs", install: true, uninstall: true) {
    	// Get the list of routines the user has
        def actions = location.helloHome?.getPhrases()*.label
        if (actions) {
        	actions.sort()
        }

		section("The Master switch whose on and/or off buttons will serve as toggles") {
        paragraph "NOTE: Plain on/off switches are preferable to dimmers.  Be mindful that dimmers may trigger unexpected toggles when turned off or dimmed to 0 (zero).  You've been warned!"
            input "master", "capability.switch", title: "Select", required: true
        }

        section("Redundant OFF presses will toggle") {
            input "offSlaves", "capability.switch", multiple: true, required: false, title: "Select"
        }
        
        section("Redundant ON presses will toggle") {
            input "onSlaves", "capability.switch", multiple: true, required: false, title: "Select"
        }
		
        if(actions) {
        	actions.sort() // Make them alphabetical
            
            section("Redundant OFF presses will run routines") {
        		input "offRoutine", "enum", title: "Select an action to execute", options: actions, required: false
			}
	        section("Redundant ON presses will run routines") {
        		input "onRoutine", "enum", title: "Select an action to execute", options: actions, required: false
            }
		}
        
        section([mobileOnly:true]) {
		label title: "Assign a name", required: false
        }
	}
}


def installed(){
    subscribe(master, "switch", switchHandler, [filterEvents: false])
}

def updated(){
    unsubscribe()
    subscribe(master, "switch", switchHandler, [filterEvents: false])
}

def switchHandler(evt) {

    if (evt.isPhysical()) {

        boolean isStateChange = evt.isStateChange()
        log.debug "Master Switch Changed State: ${isStateChange}"

        def state = master.latestState("switch").value
        log.debug "Master Switch Latest State: ${state}"
        
        if (!isStateChange) {
            log.debug "Press is redundant, toggling slaves associated with the \"${state}\" event"
            if(state == "on") {
            	onSlaves*.on()
                location.helloHome?.execute(settings.onRoutine)
            } else {
            	offSlaves*.off()
                location.helloHome?.execute(settings.offRoutine)
            }
        }
    }	
}

