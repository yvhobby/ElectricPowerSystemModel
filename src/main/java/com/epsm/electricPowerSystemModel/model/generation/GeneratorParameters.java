package com.epsm.electricPowerSystemModel.model.generation;

import com.epsm.electricPowerSystemModel.model.bothConsumptionAndGeneration.MessageInclusion;

public class GeneratorParameters extends MessageInclusion{

	public GeneratorParameters(int generatorNumber, float nominalPowerInMW, float minimalTechnologyPower) {
		super(generatorNumber);
		this.nominalPowerInMW = nominalPowerInMW;
		this.minimalTechnologyPower = minimalTechnologyPower;
	}

	private float nominalPowerInMW;
	private float minimalTechnologyPower;

	public float getNominalPowerInMW() {
		return nominalPowerInMW;
	}

	public float getMinimalTechnologyPower() {
		return minimalTechnologyPower;
	}
}