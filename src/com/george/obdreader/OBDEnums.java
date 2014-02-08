package com.george.obdreader;

public enum OBDEnums {
	
	MONITOR_STATUS("01 01",4,R.string.obd_0101,-1,-1,"",-1),
	FREEZE_DTC("01 02",2,R.string.obd_0102,-1,-1,"",-1),
	ENGINE_LOAD("01 04",1,R.string.obd_0104,0,100,"%",1),
	ENGINE_COOLANT_TEMP("01 05",1,R.string.obd_0105,0,100,"°C",2),
	SHORT_TERM_BANK_1("01 06",1,R.string.obd_0106,-100,99.22f,"%",3),
	LONG_TERM_BANK_1("01 07",1,R.string.obd_0107,-100,99.22f,"%",3),
	SHORT_TERM_BANK_2("01 08",1,R.string.obd_0108,-100,99.22f,"%",3),
	LONG_TERM_BANK_2("01 09",1,R.string.obd_0109,-100,99.22f,"%",3),
	FUEL_PRESSURE("01 0A",1,R.string.obd_010A,0,765.22f,"kPa",4),
	             
	INTAKE_MANIFOLD_PRESSURE("01 0B",1,R.string.obd_010B ,0,255,"kPa",5),                        
	ENGINE_RPM("01 0C",2,R.string.obd_010C ,0,16383.75f,"rpm",6),                             
	SPEED("01 0D",1,R.string.obd_010D ,0,255,"km/h",5),                                  
	TIMING_ADVANCE("01 0E",1,R.string.obd_010E ,-64,63.5f,"°",7),         
	AIR_INTAKE_TEMP("01 0F",1,R.string.obd_010F ,-40,215,"°C",2),                                 
	MAF("01 10",2,R.string.obd_0110 ,0,655.35f,"grams/sec",16),                          
	THROTTLE_POS("01 11",1,R.string.obd_0111 ,0,100, "%",1),                                    
//	("01 12",1,R.string.obd_0112 ,,,"",-1),                                          
//	("01 13",1,R.string.obd_0113 ,,,"",-1), 
	SHORT_TERM_BANK_1_SENSOR_1("01 14",2,R.string.obd_0114 ,-100,99.2f,"%",9),                                 
	SHORT_TERM_BANK_1_SENSOR_2("01 15",2,R.string.obd_0115 ,-100,99.2f,"%",9),                                 
	SHORT_TERM_BANK_1_SENSOR_3("01 16",2,R.string.obd_0116 ,-100,99.2f,"%",9),                                 
	SHORT_TERM_BANK_1_SENSOR_4("01 17",2,R.string.obd_0117 ,-100,99.2f,"%",9),                                 
	SHORT_TERM_BANK_2_SENSOR_1("01 18",2,R.string.obd_0118 ,-100,99.2f,"%",9),                                 
	SHORT_TERM_BANK_2_SENSOR_2("01 19",2,R.string.obd_0119 ,-100,99.2f,"%",9),                                 
	SHORT_TERM_BANK_2_SENSOR_3("01 1A",2,R.string.obd_011A ,-100,99.2f,"%",9),                                 
	SHORT_TERM_BANK_2_SENSOR_4("01 1B",2,R.string.obd_011B ,-100,99.2f,"%",9),
//	("01 1C",1,R.string.obd_011C ,,,"",-1),                                          
//	("01 1D",1,R.string.obd_011D ,,,"",-1),                                          
//	("01 1E",1,R.string.obd_011E ,,,"",-1), 
	RUN_TIME("01 1F",2,R.string.obd_011F ,0,65535,"seconds",10),
//	("01 20",4,R.string.obd_0120 ,,,"",-1), 
	DISTANCE_TRAVELED_MIL("01 21",2,R.string.obd_0121 ,0,65535,"km",10),                                 
	FUEL_RAIL_PRESSURE_MANIFOLD_VACUUM("01 22",2,R.string.obd_0122 ,0,5177.265f,"kPa",11),                              
	FUEL_RAIL_PRESSURE_DIRECT_INJECT("01 23",2,R.string.obd_0123 ,0,655350,"kPa",12),                       
	O2S1_WR_LAMBDA_V("01 24",4,R.string.obd_0124 ,0,7.999f,"V",14),                                   
	O2S2_WR_LAMBDA_V("01 25",4,R.string.obd_0125 ,0,8f,"V",14),                                       
	O2S3_WR_LAMBDA_V("01 26",4,R.string.obd_0126 ,0,8f,"V",14),                                       
	O2S4_WR_LAMBDA_V("01 27",4,R.string.obd_0127 ,0,8f,"V",14),                                       
	O2S5_WR_LAMBDA_V("01 28",4,R.string.obd_0128 ,0,8f,"V",14),                                       
	O2S6_WR_LAMBDA_V("01 29",4,R.string.obd_0129 ,0,8f,"V",14),                                       
	O2S7_WR_LAMBDA_V("01 2A",4,R.string.obd_012A ,0,8f,"V",14),                                       
	O2S8_WR_LAMBDA_V("01 2B",4,R.string.obd_012B ,0,8f,"V",14),                                       
	COMMANDED_EGR("01 2C",1,R.string.obd_012C ,0,100, "%",1),                                    
	EGR_ERROR("01 2D",1,R.string.obd_012D ,-100,99.22f, "%",3),                               
	COMMANDED_EVAPORATIVE_PURGE("01 2E",1,R.string.obd_012E ,0,100, "%",1),                                    
	FUEL_LEVEL_INPUT("01 2F",1,R.string.obd_012F ,0,100, "%",1),                                    
	WARM_UPS_CODES_CLEARED("01 30",1,R.string.obd_0130 ,0,255,"",5),                                   
	DISTANCE_TRAVELED_CODES_CLEARED("01 31",2,R.string.obd_0131 ,0,65535,"km",10),                                 
	SYSTEM_VAPOR_PRESSURE("01 32",2,R.string.obd_0132 ,-8192,8192,"Pa",17),                             
	BAROMETRIC_PRESSURE("01 33",1,R.string.obd_0133 ,0,255,"kPa",5),                                  
	O2S1_WR_LAMBDA_C("01 34",4,R.string.obd_0134 ,-128,127.99f,"mA",18),                              
	O2S2_WR_LAMBDA_C("01 35",4,R.string.obd_0135 ,-128,128,"mA",18),                                 
	O2S3_WR_LAMBDA_C("01 36",4,R.string.obd_0136 ,-128,128,"mA",18),                                 
	O2S4_WR_LAMBDA_C("01 37",4,R.string.obd_0137 ,-128,128,"mA",18),                                 
	O2S5_WR_LAMBDA_C("01 38",4,R.string.obd_0138 ,-128,128,"mA",18),                                 
	O2S6_WR_LAMBDA_C("01 39",4,R.string.obd_0139 ,-128,128,"mA",18),                                 
	O2S7_WR_LAMBDA_C("01 3A",4,R.string.obd_013A ,-128,128,"mA",18),                                 
	O2S8_WR_LAMBDA_C("01 3B",4,R.string.obd_013B ,-128,128,"mA",18),                                 
	CATALYST_TEMPERATURE_BANK_1_SENSOR_1("01 3C",2,R.string.obd_013C ,-40,6513.5f,"°C",19),                             
	CATALYST_TEMPERATURE_BANK_2_SENSOR_1("01 3D",2,R.string.obd_013D ,-40,6513.5f,"°C",19),                             
	CATALYST_TEMPERATURE_BANK_1_SENSOR_2("01 3E",2,R.string.obd_013E ,-40,6513.5f,"°C",19),                             
	CATALYST_TEMPERATURE_BANK_2_SENSOR_2("01 3F",2,R.string.obd_013F ,-40,6513.5f,"°C",19),
//	("01 40",4,R.string.obd_0140 ,,,"",-1),                                          
//	("01 41",4,R.string.obd_0141 ,,,"",-1),  
	CONTROL_MODULE_VOLTAGE("01 42",2,R.string.obd_0142 ,0,65.535f,"V",20),                                  
	ABSOLUTE_LOAD_VALUE("01 43",2,R.string.obd_0143 ,0,25700, "%",21),                                 
	COMMAND_EQUIVALENCE_RATIO("01 44",2,R.string.obd_0144 ,0,2,"N/A",22),                                     
	RELATIVE_THROTTLE_POSITION("01 45",1,R.string.obd_0145 ,0,100, "%",1),                                    
	AMBIENT_AIR_TEMPERATURE("01 46",1,R.string.obd_0146 ,-40,215,"°C",2),                                 
	ABSOLUTE_THROTTLE_POSITION_B("01 47",1,R.string.obd_0147 ,0,100, "%",1),                                    
	ABSOLUTE_THROTTLE_POSITION_C("01 48",1,R.string.obd_0148 ,0,100, "%",1),                                    
	ABSOLUTE_THROTTLE_POSITION_D("01 49",1,R.string.obd_0149 ,0,100, "%",1),                                    
	ABSOLUTE_THROTTLE_POSITION_E("01 4A",1,R.string.obd_014A ,0,100, "%",1),                                    
	ABSOLUTE_THROTTLE_POSITION_F("01 4B",1,R.string.obd_014B ,0,100, "%",1),                                    
	COMMANDED_THROTTLE_ACTUATOR("01 4C",1,R.string.obd_014C ,0,100, "%",1),                                    
	TIME_RUN_WITH_MIL("01 4D",2,R.string.obd_014D ,0,65535,"minutes",10),                            
	TIME_SINCE_TROUBLE_CODES_CLEARED("01 4E",2,R.string.obd_014E ,0,65535,"minutes",10),                            
//	("01 4F",4,R.string.obd_014F ,0, 0, 0, 0,255, 255, 255, 2550,, V, mA, kPa",-1), 
	MAXIMUM_VALUE_AIR_FLOW("01 50",4,R.string.obd_0150 ,0,2550,"g/s",23),                                  
//	("01 51",1,R.string.obd_0151 ,,,"",-1),                                          
	ETHANOL_FUEL("01 52",1,R.string.obd_0152 ,0,100, "%",1),                                    
	ABSOLUTE_EVAP_SYSTEM_VAPOR_PRESSURE("01 53",2,R.string.obd_0153 ,0,327675,"kPa",24),                               
	EVAP_SYSTEM_VAPOR_PRESSURE("01 54",2,R.string.obd_0154 ,-32767,32768,"Pa",25),                           
	SECONDARY_OXYGEN_SENSOR_SHORT_TERM_BANK_1_BANK_3("01 55",2,R.string.obd_0155 ,-100,99.22f, "%",26),                               
	SECONDARY_OXYGEN_SENSOR_LONG_TERM_BANK_1_BANK_3("01 56",2,R.string.obd_0156 ,-100,99.22f, "%",26),                               
	SECONDARY_OXYGEN_SENSOR_SHORT_TERM_BANK_2_BANK_4("01 57",2,R.string.obd_0157 ,-100,99.22f, "%",26),                               
	SECONDARY_OXYGEN_SENSOR_LONG_TERM_BANK_2_BANK_4("01 58",2,R.string.obd_0158 ,-100,99.22f, "%",26),                               
	FUEL_RAIL_PRESSURE("01 59",2,R.string.obd_0159 ,0,655350,"kPa",12),                               
	RELATIVE_ACCELERATOR_PEDAL_POSITION("01 5A",1,R.string.obd_015A ,0,100, "%",1),                                    
	HYBRID_BATTERY_PACK_REMAINING_LIFE("01 5B",1,R.string.obd_015B ,0,100, "%",1),                                    
	ENGINE_OIL_TEMPERATURE("01 5C",1,R.string.obd_015C ,-40,210,"°C",2),                                 
	FUEL_INJECTION_TIMING("01 5D",2,R.string.obd_015D ,-210.00f,301.992f,"°",27),                          
	ENGINE_FUEL_RATE("01 5E",2,R.string.obd_015E ,0,3212.75f,"L/h",28),                               
//	("01 5F",1,R.string.obd_015F ,,,"",-1),                                          
//	("01 60",4,R.string.obd_0160 ,,,"",-1),                                          
	DRIVER_DEMAND_ENGINE_TORQUE("01 61",1,R.string.obd_0161 ,-125,125, "%",29),                                 
	ACTUAL_ENGINE_TORQUE("01 62",1,R.string.obd_0162 ,-125,125, "%",29),                                 
	ENGINE_REFERENCE_TORQUE("01 63",2,R.string.obd_0163 ,0,65535,"Nm",10),                                 
	ENGINE_PERCENT_TORQUE_DATA("01 64",5,R.string.obd_0164 ,-125,125, "%",29);    
	private String command;
	private int resultSize;
	private int desc;
	private float min;
	private float max;
	private String unit;
	private int formula;
	
	OBDEnums( String command, int resultSize, int desc,
			float min, float max, String unit, int formula) {
		this.command = command;
		this.resultSize = resultSize;
		this.desc = desc;
		this.min = min;
		this.max = max;
		this.unit = unit;
		this.formula = formula;
	}
	
	public String getCommand(){
		return command;
	}

	public int getResultSize() {
		return resultSize;
	}


	public int getDesc() {
		return desc;
	}


	public float getMin() {
		return min;
	}


	public float getMax() {
		return max;
	}


	public String getUnit() {
		return unit;
	}


	public int getFormula() {
		return formula;
	}

	
	
	
	
	

}
