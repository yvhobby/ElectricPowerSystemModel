package com.epsm.electricPowerSystemModel.model.consumption;

import java.time.LocalTime;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epsm.electricPowerSystemModel.model.dispatch.ConsumerParameters;
import com.epsm.electricPowerSystemModel.model.dispatch.ConsumerState;
import com.epsm.electricPowerSystemModel.model.dispatch.Dispatcher;
import com.epsm.electricPowerSystemModel.model.dispatch.DispatcherMessage;
import com.epsm.electricPowerSystemModel.model.dispatch.PowerObjectParameters;
import com.epsm.electricPowerSystemModel.model.dispatch.PowerObjectState;
import com.epsm.electricPowerSystemModel.model.generalModel.ElectricPowerSystemSimulation;
import com.epsm.electricPowerSystemModel.model.generalModel.TimeService;

public class ShockLoadConsumer extends Consumer{
	private int maxWorkDurationInSeconds;
	private int maxPauseBetweenWorkInSeconds;
	private float maxLoad;
	private LocalTime timeToTurnOn;
	private LocalTime timeToTurnOff;
	private boolean isTurnedOn;
	private LocalTime currentTime;
	private float currentLoad;
	private float currentFrequency;
	private volatile ConsumerState state;
	private Random random = new Random();
	private Logger logger;
	
	public ShockLoadConsumer(ElectricPowerSystemSimulation simulation, TimeService timeService,
			Dispatcher dispatcher, 	Class<? extends DispatcherMessage>  expectedMessageType) {
		
		super(simulation, timeService, dispatcher, expectedMessageType); 

		logger = LoggerFactory.getLogger(ShockLoadConsumer.class);
		logger.info("Shock load consumer created with id {}.", id);
	}
	
	@Override
	public float calculateCurrentLoadInMW(){
		getNecessaryParametersFromPowerSystem();

		if(isTurnedOn){
			if(IsItTimeToTurnOff()){
				turnOffAndSetTimeToTurnOn();
			}
		}else{
			if(IsItTimeToTurnOn()){
				turnOnAndSetTimeToTurnOff();
			}
		}
		
		if(currentLoad != 0){
			currentLoad = calculateLoadCountingFrequency(currentLoad, currentFrequency);
		}
		
		state = prepareState(currentTime, currentLoad);
		
		return currentLoad;
	}

	private void getNecessaryParametersFromPowerSystem(){
		currentTime = simulation.getTimeInSimulation();
		currentFrequency = simulation.getFrequencyInPowerSystem();
	}
	
	private boolean IsItTimeToTurnOn(){
		if(isItFirstTurnOn()){
			setTimeToTurnOn();
			return false;
		}else{
			return timeToTurnOn.isBefore(currentTime);
		}
	}
	
	private boolean isItFirstTurnOn(){
		return timeToTurnOn == null;
	}
	
	private void turnOnAndSetTimeToTurnOff(){
		turnOnWithRandomLoadValue();
		setTimeToTurnOff();
	}
	
	private void turnOnWithRandomLoadValue(){
		float halfOfMaxLoad = maxLoad / 2;
		currentLoad = halfOfMaxLoad + halfOfMaxLoad * random.nextFloat();
		isTurnedOn = true;
	}
	
	private void setTimeToTurnOff(){
		float halfOfTurnedOnDuration = maxWorkDurationInSeconds / 2; 
		timeToTurnOff = currentTime.plusSeconds(
				(long)(halfOfTurnedOnDuration + halfOfTurnedOnDuration * random.nextFloat()));
	}
	
	private boolean IsItTimeToTurnOff(){
		return timeToTurnOff.isBefore(currentTime);
	}
	
	private void turnOffAndSetTimeToTurnOn(){
		turnOff();
		setTimeToTurnOn();
	}
	
	private void turnOff(){
		currentLoad = 0;
		isTurnedOn = false;
	}
	
	private void setTimeToTurnOn(){
		float halfOfTurnedOffDuration = maxPauseBetweenWorkInSeconds / 2; 
		timeToTurnOn = currentTime.plusSeconds(
				(long)(halfOfTurnedOffDuration + halfOfTurnedOffDuration * random.nextFloat()));
	}
	
	@Override
	public PowerObjectState getState() {
		return state;
	}
	
	public void setMaxWorkDurationInSeconds(int WorkDurationInSeconds) {
		this.maxWorkDurationInSeconds = WorkDurationInSeconds;
	}

	public void setMaxPauseBetweenWorkInSeconds(int durationBetweenWorkInSeconds) {
		this.maxPauseBetweenWorkInSeconds = durationBetweenWorkInSeconds;
	}
		
	public void setMaxLoad(float maxLoad) {
		this.maxLoad = maxLoad;
	}

	@Override
	protected void processDispatcherMessage(DispatcherMessage message) {
		//TODO turn off/on user by dispatcher command. 
	}

	@Override
	public PowerObjectParameters getParameters() {
		//for now just stub
		return new ConsumerParameters(1);
	}
}
