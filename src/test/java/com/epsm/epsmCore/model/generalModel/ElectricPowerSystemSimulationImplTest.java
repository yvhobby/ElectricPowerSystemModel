package com.epsm.epsmCore.model.generalModel;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.epsm.epsmCore.model.constantsForTests.TestsConstants;
import com.epsm.epsmCore.model.consumption.Consumer;
import com.epsm.epsmCore.model.dispatch.Dispatcher;
import com.epsm.epsmCore.model.generation.PowerStation;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PowerStation.class)
public class ElectricPowerSystemSimulationImplTest {
	private ElectricPowerSystemSimulationImpl simulation;
	private TimeService timeService;
	private Dispatcher dispatcher;
	private PowerStation station;
	private Consumer consumer;
	private float previousFrequency;
	private float currentFrequency;
	
	@Before
	public void setUp(){
		timeService = new TimeService();
		dispatcher = mock(Dispatcher.class);
		simulation = new ElectricPowerSystemSimulationImpl(timeService, dispatcher,
				TestsConstants.START_DATETIME);
		station = PowerMockito.mock(PowerStation.class);
		consumer = PowerMockito.mock(Consumer.class);
		when(station.getId()).thenReturn(1L);
		when(consumer.getId()).thenReturn(2L);
		
		simulation.addPowerStation(station);
		simulation.addPowerConsumer(consumer);
	}
	
	@Test
	public void timeGoesInTheSimulation(){
		LocalDateTime previousTime;
		LocalDateTime nextTime;
		
		for(int i = 0; i < 10 ;i++){
			previousTime = simulation.getDateTimeInSimulation();
			simulation.calculateNextStep();
			nextTime = simulation.getDateTimeInSimulation();
			
			Assert.assertTrue(previousTime.isBefore(nextTime));
		}
	}
	
	@Test
	public void FrequencyDecreasesIfLoadHigherThanGeneration(){
		when(station.calculatePowerBalance()).thenReturn(99f);
		when(consumer.calculatePowerBalance()).thenReturn(-100f);

		for(int i = 0; i < 100; i++){			
			rememberOldFrequencyAndDoNextStep();
			
			Assert.assertTrue(previousFrequency > currentFrequency);
		}
	}
	
	private void rememberOldFrequencyAndDoNextStep(){
		previousFrequency = simulation.getFrequencyInPowerSystem();
		simulation.calculateNextStep();
		currentFrequency = simulation.getFrequencyInPowerSystem();
	}
	
	@Test
	public void FrequencyIncreasesIfLoadLessThanGeneration(){
		when(station.calculatePowerBalance()).thenReturn(100f);
		when(consumer.calculatePowerBalance()).thenReturn(99f);
		
		for(int i = 0; i < 100; i++){			
			rememberOldFrequencyAndDoNextStep();
			
			Assert.assertTrue(previousFrequency < currentFrequency);
		}
	}
	
	@Test
	public void FrequencyIsConstantIfLoadEqualsToGeneration(){
		when(station.calculatePowerBalance()).thenReturn(100f);
		when(consumer.calculatePowerBalance()).thenReturn(-100f);
		
		for(int i = 0; i < 100; i++){			
			rememberOldFrequencyAndDoNextStep();
			
			Assert.assertTrue(previousFrequency == currentFrequency);
		}
	}
}
