package test.java.com.epsm.electricPowerSystemModel.model.manualTesting;

import java.util.Formatter;

import main.java.com.epsm.electricPowerSystemModel.model.dispatch.SimulationReport;
import main.java.com.epsm.electricPowerSystemModel.model.generalModel.ElectricPowerSystemSimulationImpl;

public class PowerSystemSimulationManualTest {
	/*private Formatter fmt;
	private ElectricPowerSystemSimulationImpl simulation;
	private DispatcherTestImpl dispatcher = new DispatcherTestImpl(); 
	private SimulationReport report;
	private StringBuilder sb;
	private int counter;
	private final int INTERVAL_BETWEEN_PRINTS = 10;
	private final int PAUSE_BETWEEN_CALCULATING_STEPS_IN_MS = 100;
	
	public static void main(String[] args) {
		PowerSystemSimulationManualTest simulation = new PowerSystemSimulationManualTest();
		
		simulation.initialize();
		simulation.go();
	}
	
	private void initialize(){
		simulation = new ElectricPowerSystemSimulationImpl();
		sb = new StringBuilder();
		fmt = new Formatter(sb);
		
		new ModelInitializator().initialize(simulation, dispatcher);
	}
	
	public void go(){
		//for(int i = 0; i < 1_000_000; i++){
		while(true){
			report = simulation.calculateNextStep();
			
			if(isItTimeToPrint()){
				System.out.println(getSimulationMessage());
				System.out.println(dispatcher.getMessage());
			}
			
			pause();
		}
	}
	
	private boolean isItTimeToPrint(){
		
		if(INTERVAL_BETWEEN_PRINTS == 0){
			return true;
		}else if(counter % INTERVAL_BETWEEN_PRINTS == 0){
			counter = 1;
			return true;
		}else{
			counter ++;
			return false;
		}
	}
	
	private String getSimulationMessage(){
		sb.setLength(0);
		fmt.format(
				"%12s"+ 
				", totalGeneration= %6.2f, totalConsumption= %6.2f ,frequency= %5.2f", 
				report.getCurrentTimeInSimulation(), report.getTotalGeneration(),
				report.getTotalLoad(), report.getFrequencyInPowerSystem());
				
		return sb.toString();
	}
	
	private void pause(){
		if(PAUSE_BETWEEN_CALCULATING_STEPS_IN_MS != 0){
			try {
				Thread.sleep(PAUSE_BETWEEN_CALCULATING_STEPS_IN_MS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}*/
}