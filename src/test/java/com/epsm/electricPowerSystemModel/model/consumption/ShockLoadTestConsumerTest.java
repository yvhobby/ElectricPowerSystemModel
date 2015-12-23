package test.java.com.epsm.electricPowerSystemModel.model.consumption;

import static org.mockito.Mockito.*;

import java.time.Duration;
import java.time.LocalTime;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import main.java.com.epsm.electricPowerSystemModel.model.consumption.ShockLoadConsumer;
import main.java.com.epsm.electricPowerSystemModel.model.generalModel.ElectricPowerSystemSimulationImpl;
import main.java.com.epsm.electricPowerSystemModel.model.generalModel.GlobalConstatnts;

public class ShockLoadTestConsumerTest {
	private ElectricPowerSystemSimulationImpl simulation;
	private ShockLoadConsumer consumer;
	float previousLoad;
	float currentLoad;
	private LocalTime turnOnTime;
	private LocalTime turnOffTime;
	private final int WORK_TIME = 300; 
	private final int PAUSE_TIME = 500; 
	
	@Before
	public void initialize(){
		simulation = spy(new ElectricPowerSystemSimulationImpl());
		consumer = new ShockLoadConsumer(1);
		turnOnTime = null;
		turnOffTime = null;
		
		when(simulation.getFrequencyInPowerSystem()).thenReturn(GlobalConstatnts.STANDART_FREQUENCY);
		
		consumer.setMaxLoad(100f);
		consumer.setMaxWorkDurationInSeconds(WORK_TIME);
		consumer.setMaxPauseBetweenWorkInSeconds(PAUSE_TIME);
		consumer.setElectricalPowerSystemSimulation(simulation);
		consumer.setDegreeOfDependingOnFrequency(2);
	}
	
	@Test
	public void loadChargesInstantlyAndThenConstant(){
		boolean loadChangedAfterTurnOn = true;
		
		for(int i = 0; i < 1_000_000; i++){
			rememberCurrentLoadAsPreviousAndDoNextStep();
			
			if(wasLoadTurnedOn()){
				rememberCurrentLoadAsPreviousAndDoNextStep();
				loadChangedAfterTurnOn = wasLoadChanged();
				break;
			}
		}
		
		Assert.assertFalse(loadChangedAfterTurnOn);
	}
	
	private boolean wasLoadTurnedOn(){
		return previousLoad == 0 && currentLoad > 0;
	}
	
	private boolean wasLoadChanged(){
		return currentLoad != previousLoad;
	}
	
	private void rememberCurrentLoadAsPreviousAndDoNextStep(){
		previousLoad = currentLoad;
		simulation.calculateNextStep();
		currentLoad = consumer.calculateCurrentLoadInMW();
	}
	
	@Test
	public void loadDischargesInstantlyAndThenConstant(){
		boolean loadChangedAfterTurnOff = true;
		
		for(int i = 0; i < 1_000_000; i++){
			rememberCurrentLoadAsPreviousAndDoNextStep();
			
			if(wasLoadTurnedOff()){
				rememberCurrentLoadAsPreviousAndDoNextStep();
				loadChangedAfterTurnOff = wasLoadChanged();
				break;
			}
		}
		
		Assert.assertFalse(loadChangedAfterTurnOff);
	}
	
	private boolean wasLoadTurnedOff(){
		return previousLoad > 0 &&  currentLoad == 0;
	}
	
	@Test
	public void WorkDurationConformsfromHalfToTheWholeOfSet(){
		for(int i = 0; i < 2; i++){//too much time if more
			findTurnOnTime();
			findTurnOffTime();
			long duration = getAbsSecondsBetweenTwoTimes();
	
			Assert.assertTrue(turnOnTime != null && turnOffTime != null);
			Assert.assertTrue(duration >= WORK_TIME / 2 && duration <= WORK_TIME);
		}
	}
	
	private void findTurnOnTime(){
		for(int i = 0; i < 1_000_000; i++){
			rememberCurrentLoadAsPreviousAndDoNextStep();
			if(wasLoadTurnedOn()){
				turnOnTime = simulation.getTime();
				break;
			}
		}
	}
	
	private void findTurnOffTime(){
		for(int i = 0; i < 1_000_000; i++){
			rememberCurrentLoadAsPreviousAndDoNextStep();
			if(wasLoadTurnedOff()){
				turnOffTime = simulation.getTime();
				break;
			}
		}
	}
	
	private long getAbsSecondsBetweenTwoTimes(){
		Duration duration = Duration.between(turnOnTime, turnOffTime);
		long seconds = Math.abs(duration.getSeconds());
		return seconds;
	}
	
	@Test
	public void pauseBetweenWorksConformsFromHalfToWholeOfSet(){
		for(int i = 0; i < 2; i++){//too much time if more
			findTurnOffTime();
			findTurnOnTime();
			long duration = getAbsSecondsBetweenTwoTimes();
	
			Assert.assertTrue(turnOnTime != null && turnOffTime != null);
			Assert.assertTrue(duration >= PAUSE_TIME / 2 && duration <= PAUSE_TIME);
		}
	}
	
	@Test
	public void LoadDependsOnFrequency(){
		findTurnOnTime();
		prepareMockSimulationWithDecreasingFrequency();
		rememberCurrentLoadAsPreviousAndDoNextStep();
		
		Assert.assertTrue(turnOnTime != null);
		Assert.assertTrue(previousLoad > currentLoad);
	}
	
	public void prepareMockSimulationWithDecreasingFrequency(){
		when(simulation.getFrequencyInPowerSystem()).thenReturn(49.99f);
	}
}