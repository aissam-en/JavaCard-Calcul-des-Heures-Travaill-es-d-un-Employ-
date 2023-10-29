
package employer_javacard;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;

public class Employer_javacard extends Applet {
	
    private byte nbr_hours = 0;
    private byte start_hour = 0;
    private byte start_min = 0;
    private byte stop_hour = 0;
    private byte stop_min = 0;
    
    private static final byte CLA = (byte) 0xB0;
    
    private static final byte CALCUL_HEURES = (byte)0x01;
    private static final byte AFFICHE_HEURES = (byte)0x02;
    
    private static final byte A_ZERO_NBR_H = (byte)0x03;
    private static final byte A_ZERO = (byte)0x04;
    
    private static final byte START_TIME_H = (byte)0x05;
    private static final byte START_TIME_MIN = (byte)0x06;    
    private static final byte STOP_TIME_H = (byte)0x07;
    private static final byte STOP_TIME_MIN = (byte)0x08;

	private Employer_javacard() {
	}

	public static void install(byte bArray[], short bOffset, byte bLength) throws ISOException {
		new Employer_javacard().register();
	}

	public void process(APDU apdu) throws ISOException {
		if (this.selectingApplet()) { return; }
		
		byte[] buffer = apdu.getBuffer(); 
		
		if(buffer[ISO7816.OFFSET_CLA] != CLA) { ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
		}
		
		switch(buffer[ISO7816.OFFSET_INS]) {
			case CALCUL_HEURES: 
				short diff_h = (short) (stop_hour - start_hour); //int + no casting
				diff_h = (short) (diff_h*60);   //h est en minutes  //no casting
				short diff_min = (short) (diff_h + (stop_min - start_min)); //int + no casting
				
				nbr_hours = (byte) (diff_min / 60);
				if (diff_min % 60 >= 30) {
					nbr_hours += 1;
				}	
				
				break;
			case AFFICHE_HEURES: 			
				buffer[0] = nbr_hours; 
				apdu.setOutgoingAndSend((short)0, (short)1);
				break;
			
			case A_ZERO_NBR_H: 
				nbr_hours = 0;
				break;
				
			case A_ZERO: 
			    start_hour = 0;
			    start_min = 0;
			    stop_hour = 0;
			    stop_min = 0;
				break;
				
			case START_TIME_H: 
				apdu.setIncomingAndReceive();
				start_hour = buffer[ISO7816.OFFSET_CDATA];
				break;
				
			case START_TIME_MIN: 
				apdu.setIncomingAndReceive();
				start_min = buffer[ISO7816.OFFSET_CDATA];
				break;
				
			case STOP_TIME_H: 
				apdu.setIncomingAndReceive();
				stop_hour = buffer[ISO7816.OFFSET_CDATA];
				break;
				
			case STOP_TIME_MIN: 
				apdu.setIncomingAndReceive();
				stop_min = buffer[ISO7816.OFFSET_CDATA];
				break;

		}
		
	}
	
}
