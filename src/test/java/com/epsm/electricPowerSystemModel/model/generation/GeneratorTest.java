package test.java.com.epsm.electricPowerSystemModel.model.generation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import main.java.com.epsm.electricPowerSystemModel.model.dispatch.GeneratorState;
import main.java.com.epsm.electricPowerSystemModel.model.generation.AstaticRegulator;
import main.java.com.epsm.electricPowerSystemModel.model.generation.Generator;
import main.java.com.epsm.electricPowerSystemModel.model.generation.StaticRegulator;

public class GeneratorTest{
	private Generator generator;
	private StaticRegulator staticRegulator;
	private AstaticRegulator astaticRegulator;
	private GeneratorState state;
	private final int GENERATOR_NUMBER = 1;
	private final float GENERATOR_GENERATION = 1000f; 
	
	@Before
	public void initialize(){
		generator = new Generator(GENERATOR_NUMBER);
		staticRegulator = mock(StaticRegulator.class);
		
		when(staticRegulator.getGeneratorPowerInMW()).thenReturn(GENERATOR_GENERATION);
		generator.setStaticRegulator(staticRegulator);
		generator.turnOnGenerator();
	}
	
	@Test
	public void GeneratorPowerIsZeroIfItTurnedOff(){
		generator.turnOffGenerator();
		
		Assert.assertEquals(0, generator.calculateGeneration(), 0);
	}
	
	@Test
	public void isGeneratorPowerEqualsToControlUnitIfItTurnedOn(){
		generator.turnOnGenerator();
		
		Assert.assertEquals(staticRegulator.getGeneratorPowerInMW(), generator.calculateGeneration(), 0);
	}
	
	@Test
	public void DoesAstacicRegulatorUsesIfItTurnedOn(){
		prepareMockAstaticRegulator();
		generator.turnOnAstaticRegulation();
		generator.calculateGeneration();
		
		verify(astaticRegulator, times(1)).verifyAndAdjustPowerAtRequiredFrequency();
	}
	
	private void prepareMockAstaticRegulator(){
		astaticRegulator = mock(AstaticRegulator.class);
		generator.setAstaticRegulator(astaticRegulator);
	}
	
	@Test
	public void generatorMakesRightItsStateObject(){
		prepareState();
		
		Assert.assertEquals(GENERATOR_NUMBER, state.getGeneratorNumber());
		Assert.assertEquals(GENERATOR_GENERATION, state.getGenerationInWM(), 0);
	}
	
	private void prepareState(){
		calculateGenerations();
		getState();
	}
	
	private void calculateGenerations(){
		generator.calculateGeneration();
	}
	
	private void getState(){
		state = (GeneratorState) generator.getState();
	}
}
