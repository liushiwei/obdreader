/*
 * TODO put header 
 */
package eu.lighthouselabs.obd.commands.fuel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.george.obdreader.Log;

import eu.lighthouselabs.obd.commands.ObdCommand;
import eu.lighthouselabs.obd.commands.SpeedObdCommand;
import eu.lighthouselabs.obd.commands.control.CommandEquivRatioObdCommand;
import eu.lighthouselabs.obd.commands.engine.EngineRPMObdCommand;
import eu.lighthouselabs.obd.commands.pressure.IntakeManifoldPressureObdCommand;
import eu.lighthouselabs.obd.commands.temperature.AirIntakeTemperatureObdCommand;
import eu.lighthouselabs.obd.enums.AvailableCommandNames;

/**
 * TODO put description
 */
public class FuelEconomyWithoutMAFObdCommand extends ObdCommand {
	
	public static final double AIR_FUEL_RATIO = 14.64;
    public static final double FUEL_DENSITY_GRAMS_PER_LITER = 720.0;
    
    private double fuel;
	
	public FuelEconomyWithoutMAFObdCommand() {
		super("");
	}
	
	/**
	 * As it's a fake command, neither do we need to send request or read
	 * response.
	 */
	@Override
	public void run(InputStream in, OutputStream out) throws IOException,
			InterruptedException {
		// prepare variables
		EngineRPMObdCommand rpmCmd = new EngineRPMObdCommand();
		rpmCmd.run(in, out);
		rpmCmd.getFormattedResult();
		
        AirIntakeTemperatureObdCommand airTempCmd = new AirIntakeTemperatureObdCommand();
        airTempCmd.run(in, out);
        airTempCmd.getFormattedResult();
        
        SpeedObdCommand speedCmd = new SpeedObdCommand();
        speedCmd.run(in, out);
        speedCmd.getFormattedResult();
        
        CommandEquivRatioObdCommand equivCmd = new CommandEquivRatioObdCommand();
        equivCmd.run(in, out);
        equivCmd.getFormattedResult();
        
        IntakeManifoldPressureObdCommand pressCmd = new IntakeManifoldPressureObdCommand();
        pressCmd.run(in, out);
        pressCmd.getFormattedResult();
        
        double imap = rpmCmd.getRPM() * pressCmd.getMetricUnit() / airTempCmd.getKelvin();
        double intakeAir = (2.4*pressCmd.getMetricUnit())/(8.314472*(273+airTempCmd.getKelvin()))*0.725*(rpmCmd.getRPM())/(120*29);
        Log.e("Fuel", "pressCmd.getMetricUnit() = "+pressCmd.getMetricUnit()+ " airTempCmd.getKelvin() = "+airTempCmd.getKelvin()+ " rpmCmd.getRPM() = "+rpmCmd.getRPM());
        Log.e("Fuel", "intakeAir = "+intakeAir);
        fuel = (intakeAir/14.64/0.725*3.6)*100/speedCmd.getMetricSpeed();
        Log.e("Fuel", "fuel = "+fuel+" speedCmd.getMetricSpeed()="+speedCmd.getMetricSpeed());
//        double maf = (imap / 120) * (speedCmd.getMetricSpeed()/100)*()
        
	}

	@Override
	public String getFormattedResult() {
		// TODO Auto-generated method stub
		return fuel+" L/100KM";
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public AvailableCommandNames getId() {
		return AvailableCommandNames.FUEL_ECONOMY_WITHOUT_MAF;
	}
	
	

}
