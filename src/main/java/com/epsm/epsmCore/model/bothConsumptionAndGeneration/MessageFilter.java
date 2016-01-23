package com.epsm.epsmCore.model.bothConsumptionAndGeneration;

import com.epsm.epsmCore.model.consumption.Consumer;
import com.epsm.epsmCore.model.consumption.ConsumerState;
import com.epsm.epsmCore.model.consumption.ConsumptionPermissionStub;
import com.epsm.epsmCore.model.dispatch.Command;
import com.epsm.epsmCore.model.dispatch.State;
import com.epsm.epsmCore.model.generation.PowerStation;
import com.epsm.epsmCore.model.generation.PowerStationGenerationSchedule;
import com.epsm.epsmCore.model.generation.PowerStationState;

public class MessageFilter {
	private Class<? extends Command> expectedCommandClass;
	private Class<? extends State> expectedStateClass;
	
	public MessageFilter(Class<? extends PowerObject> objectClass) {
		if(objectClass == null){
			String message  = "MessageFilter constructor: object class can't be null.";
			throw new IllegalArgumentException(message);
		}
		
		if(isObjectPowerStation(objectClass)){
			expectedCommandClass = PowerStationGenerationSchedule.class;
			expectedStateClass = PowerStationState.class;
		}else if(isObjectInstanceOfConsumer(objectClass)){
			expectedCommandClass = ConsumptionPermissionStub.class;
			expectedStateClass = ConsumerState.class;
		}else{
			String message = String.format("MessageFilter constructor: %s.class is not supported.", 
					objectClass.getSimpleName());
			throw new IllegalArgumentException(message);
		}
	}
	
	private boolean isObjectPowerStation(Class<? extends PowerObject> objectClass){
		return objectClass == PowerStation.class;
	}
	
	private boolean isObjectInstanceOfConsumer(Class<? extends PowerObject> objectClass){
		return Consumer.class.isAssignableFrom(objectClass);
	}
	
	public boolean isCommandTypeAppropriate(Command command){
		if(command == null){
			String exceptionMessage = "MessageFilter isCommandTypeAppropriate(...) method:"
					+ " command can't be null.";
			throw new IllegalArgumentException(exceptionMessage);	
		}
		
		return command.getClass() == expectedCommandClass;
	}
	
	public boolean isStateTypeAppropriate(State state){
		if(state == null){
			String exceptionMessage = "MessageFilter isStateTypeAppropriate(...) method:"
					+ " state can't be null.";
			throw new IllegalArgumentException(exceptionMessage);	
		}
		
		return state.getClass() == expectedStateClass;
	}
	
	
	public String getExpectedStateClassName(){
		return expectedStateClass.getSimpleName();
	}

	public Object getExpectedCommandClassName() {
		return expectedCommandClass.getSimpleName();
	}
}