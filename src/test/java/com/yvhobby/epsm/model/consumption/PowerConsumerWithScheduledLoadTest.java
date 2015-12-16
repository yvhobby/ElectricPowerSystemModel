package test.java.com.yvhobby.epsm.model.consumption;

import java.time.LocalTime;
import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import main.java.com.yvhobby.epsm.model.bothConsumptionAndGeneration.PowerOnHoursPattern;
import main.java.com.yvhobby.epsm.model.consumption.PowerConsumer;
import main.java.com.yvhobby.epsm.model.consumption.PowerConsumerWithScheduledLoad;
import main.java.com.yvhobby.epsm.model.generalModel.ElectricPowerSystemSimulation;
import main.java.com.yvhobby.epsm.model.generalModel.GlobalConstatnts;
import main.java.com.yvhobby.epsm.model.generalModel.SimulationParameters;
import main.java.com.yvhobby.epsm.model.generation.PowerStation;

public class PowerConsumerWithScheduledLoadTest{
	private PowerConsumerWithScheduledLoad consumer;
	private ElectricPowerSystemSimulation simulation;
	private PowerOnHoursPattern pattern;
	private Random random = new Random(); 
	
	@Before
	public void initialize(){
		simulation = createPowerSystemStub();
		consumer = new PowerConsumerWithScheduledLoad();
		pattern = new PowerOnHoursPattern();
		
		consumer.setDailyPattern(pattern);
		consumer.setMaxConsumptionWithoutRandomInMW(100);
		consumer.setRandomComponentInPercent(10);
		consumer.setDegreeOfDependingOnFrequency(2);
		consumer.setElectricalPowerSystemSimulation(simulation);
	}
	
	private ElectricPowerSystemSimulation createPowerSystemStub(){
		return new ElectricPowerSystemSimulation() {
			LocalTime time = LocalTime.of(0, 0);
			float possibleFluctuations = 1.2f;
			float frequency = 50;
						
			@Override
			public LocalTime getTime() {
				return time;
			}
			
			@Override
			public float getFrequencyInPowerSystem() {
				return frequency;
			}
			
			@Override
			public SimulationParameters calculateNextStep() {
				time = time.plusNanos(100_000_000);
				frequency = (random.nextFloat() * GlobalConstatnts.STANDART_FREQUENCY * 
						possibleFluctuations);
				
				return null;
			}

			@Override
			public void addPowerConsumer(PowerConsumer powerConsumer) {
			}

			@Override
			public void addPowerStation(PowerStation powerStation) {
			}
		};
	}
	
	@Test
	public void isConsumerConsumptionIsInExpectingRange(){
		LocalTime time = simulation.getTime();
		float minPermissibleValue = 0;
		float maxPermissibleValue = 0;
		float actualValue = 0;
		int times = 2 * 24 * 60 * 60 * 10;
		
		for(int i = 0; i < times; i++){
			actualValue = consumer.getCurrentConsumptionInMW();
			minPermissibleValue = calculateMinPermissibleValue(time);
			maxPermissibleValue = calculateMaxPermissibleValue(time);
			
			Assert.assertTrue(actualValue >= minPermissibleValue);
			Assert.assertTrue(actualValue <= maxPermissibleValue);
		
			simulation.calculateNextStep();
			time = simulation.getTime();
		}
	}
	
	private float calculateMinPermissibleValue(LocalTime time){
		float minValueWithoutRandom = findMinValueWithoutRandomInMW(time);
		float minValueWithRandom = substractRandomPartInMw(minValueWithoutRandom);
		float minValueWithRandomAndCountingFrequency = 
				calculateConsumptionCountingFrequencyInMW(minValueWithRandom);
		
		return minValueWithRandomAndCountingFrequency;
	}
	
	private float findMinValueWithoutRandomInMW(LocalTime time){
		float valueOnRequestedHour = pattern.getPowerInPercentForCurrentHour(time);
		float valueOnNextHour = pattern.getPowerInPercentForCurrentHour(time.plusHours(1));
		
		return Math.min(valueOnRequestedHour, valueOnNextHour) *
				consumer.getMaxConsumptionWithoutRandomInMW() / 100;
	}
	
	private float substractRandomPartInMw(float consumption){
		return consumption - consumption * consumer.getRandomComponentInPercent() / 100;
	}
	
	private float calculateConsumptionCountingFrequencyInMW(float consumption){
		float currentFrequency = simulation.getFrequencyInPowerSystem();
		float standartFrequency = GlobalConstatnts.STANDART_FREQUENCY;
		float degreeOfDepending = consumer.getDegreeOnDependingOfFrequency();
		
		return (float)Math.pow(
				(currentFrequency / standartFrequency),	degreeOfDepending) * consumption;
	}
	
	private float calculateMaxPermissibleValue(LocalTime time){
		float maxValueWithoutRandom = findMaxValueWithoutRandomInMW(time);
		float maxValueWithRandom = addRandomPartInMw(maxValueWithoutRandom);
		float maxValueWithRandomAndCountingFrequency = 
				calculateConsumptionCountingFrequencyInMW(maxValueWithRandom);
		
		return maxValueWithRandomAndCountingFrequency;
	}
	
	private float findMaxValueWithoutRandomInMW(LocalTime time){
		float valueOnRequestedHour = pattern.getPowerInPercentForCurrentHour(time);
		float valueOnNextHour = pattern.getPowerInPercentForCurrentHour(time.plusHours(1));
		
		return Math.max(valueOnRequestedHour, valueOnNextHour) *
				consumer.getMaxConsumptionWithoutRandomInMW() / 100;
	}
	
	private float addRandomPartInMw(float consumption){
		return consumption + consumption * consumer.getRandomComponentInPercent() / 100;
	}
}
