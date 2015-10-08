/**
 *  Switch Link
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
    name: "Switch Link",
    namespace: "pranalli",
    author: "Pasquale Ranalli",
    description: "This app allows you to link a switch to one or many other lights or switches.  ",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet@2x.png"
)

preferences {

    section("The primary switch use to control others") {
        input "master", "capability.switch", title: "Select", required: true
    }

    section("Switch(es) to be controlled by the primary switch") {
        input "switches", "capability.switch", multiple: true, required: false, title: "Select"
    }
}

def installed() {
    subscribe(master, "switch", switchHandler, [filterEvents: false])
}

def updated(){
    unsubscribe()
    subscribe(master, "switch", switchHandler, [filterEvents: false])
}

def switchHandler(evt) {
    if (evt.isPhysical()) 
    	evt.value == "on" ? switches*.on() : switches*.off()
}