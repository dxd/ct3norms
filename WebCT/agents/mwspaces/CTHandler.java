package mwspaces;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import tuplespace.*;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;

import com.j_spaces.core.client.EntryArrivedRemoteEvent;

public class CTHandler extends UnicastRemoteObject implements RemoteEventListener {


	private CTsetup ct;
	
	public CTHandler(CTsetup cTsetup) throws RemoteException{ 
		ct = cTsetup;

	}

	public void notify(RemoteEvent anEvent) {

        try {
        	EntryArrivedRemoteEvent arrivedRemoteEvent =(EntryArrivedRemoteEvent) anEvent;
        	TimeEntry e = (TimeEntry) arrivedRemoteEvent.getObject();
        	System.out.println("CT notification: "+e);

        	ct.event(e);

            

        } catch (Exception anE) {
            System.out.println("Error while procession organization notification");
            anE.printStackTrace(System.out);
        }
    }
}
