package com.epsm.electricPowerSystemModel.model.generation;

import com.epsm.electricPowerSystemModel.model.bothConsumptionAndGeneration.LoadCurve;
import com.epsm.electricPowerSystemModel.model.bothConsumptionAndGeneration.MessageInclusion;

public class GeneratorGenerationSchedule extends MessageInclusion{
	private boolean generatorTurnedOn;
	private boolean astaticRegulatorTurnedOn;
	private LoadCurve generationCurve;
	
	public GeneratorGenerationSchedule(int generatorNumber, boolean isGeneratorTurnedOn,
			boolean isAstaticRegulatorTurnedOn, LoadCurve generationCurve) {
		
		super(generatorNumber);
		generatorTurnedOn = isGeneratorTurnedOn;
		astaticRegulatorTurnedOn = isAstaticRegulatorTurnedOn;
		this.generationCurve = generationCurve;
	}
	
	public boolean isGeneratorTurnedOn() {
		return generatorTurnedOn;
	}

	public boolean isAstaticRegulatorTurnedOn() {
		return astaticRegulatorTurnedOn;
	}

	public LoadCurve getCurve() {
		return generationCurve;
	}

	@Override
	public String toString() {
		stringBuilder.setLength(0);
		stringBuilder.append("Generator#");
		stringBuilder.append(inclusionNumber);
		stringBuilder.append(", turnedOn: ");
		stringBuilder.append(isGeneratorTurnedOn());
		stringBuilder.append(", astatic regulation: ");
		stringBuilder.append(astaticRegulatorTurnedOn);
		stringBuilder.append(". ");
		
		return stringBuilder.toString();
	}
}
