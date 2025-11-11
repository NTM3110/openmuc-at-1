/*
 * This file is part of OpenMUC.
 * For more information visit http://www.openmuc.org
 *
 * You are free to use code of this sample file in any
 * way you like and without any restrictions.
 *
 */
package org.openmuc.framework.app.simpledemo;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.openmuc.framework.data.DoubleValue;
import org.openmuc.framework.data.Flag;
import org.openmuc.framework.data.Record;
import org.openmuc.framework.data.StringValue;
import org.openmuc.framework.data.Value;
import org.openmuc.framework.dataaccess.Channel;
import org.openmuc.framework.dataaccess.DataAccessService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = {})
public final class SimpleDemoApp {

    private static final Logger logger = LoggerFactory.getLogger(SimpleDemoApp.class);
    private static final DecimalFormatSymbols DFS = DecimalFormatSymbols.getInstance(Locale.US);
    private static final DecimalFormat DF = new DecimalFormat("#0.000", DFS);

    // ChannelIDs, see conf/channel.xml
    private static final String CHANNEL_1 = "str1_demo_1";
    private static final String CHANNEL_2 = "str1_demo_2";
    private static final String CHANNEL_3 = "str1_demo_3";


    // With the dataAccessService you can access to your measured and control data of your devices.
    @Reference
    private DataAccessService dataAccessService;

    private Timer updateTimer;

    /**
     * Every app needs one activate method. Is is called at begin. Here you can configure all you need at start of your
     * app. The Activate method can block the start of your OpenMUC, f.e. if you use Thread.sleep().
     */
    @Activate
    private void activate() {
        logger.info("Activating Demo App");
        init();
    }

    /**
     * Every app needs one deactivate method. It handles the shutdown of your app e.g. closing open streams.
     */
    @Deactivate
    private void deactivate() {
        logger.info("Deactivating Demo App");
        logger.info("DemoApp thread interrupted: will stop");
        updateTimer.cancel();
        updateTimer.purge();
    }

    /**
     * application logic
     */
    private void init() {
        logger.info("Demo App started running..."); 
        initUpdateTimer();
    }

    private void getChannel(){
        // logger.info("Getting value from channel {}", CHANNEL);
        try {
            Channel channel1 = dataAccessService.getChannel(CHANNEL_1);
            if (channel1 == null) {
                logger.warn("Channel 1 {} not found!", CHANNEL_1);
                return;
            }
            // Channel channel2 = dataAccessService.getChannel(CHANNEL_2);
            // if (channel2 == null) {
            //     logger.warn("Channel 2 {} not found!", CHANNEL_2);
            //     return;
            // }
            // Channel channel3 = dataAccessService.getChannel(CHANNEL_3);
            // if (channel3 == null) {
            //     logger.warn("Channel 3 {} not found!", CHANNEL_3);
            //     return;
            // }

            Record firstRecord = channel1.getLatestRecord();
            // Record secondRecord = channel2.getLatestRecord();
            // Record thirdRecord = channel3.getLatestRecord();

            if (firstRecord == null) {
                logger.warn("No record found for channel {}", CHANNEL_1);
                return;
                
            }
            // if (secondRecord == null) {
            //     logger.warn("No record found for channel {}", CHANNEL_2);
            //     return;
                
            // }
            // if (thirdRecord == null) {
            //     logger.warn("No record found for channel {}", CHANNEL_3);
            //     return; 
            // }

            double newValue1 = firstRecord.getValue().asDouble();
            // double newValue2 = secondRecord.getValue().asDouble();
            // double newValue3 = thirdRecord.getValue().asDouble();

            logger.info("Value of {}: {}", CHANNEL_1, newValue1);
            // logger.info("Value of {}: {}", CHANNEL_2, newValue2);
            // logger.info("Value of {}: {}", CHANNEL_3, newValue3);
        } catch (Exception e) {
            logger.warn("Error updating channel {}", e.getMessage());
        }
    }
    /**
     * Initialize channel objects
     */
    /**
     * Apply a RecordListener to get notified if a new value is available for a channel
     */
    private void initUpdateTimer() {
        logger.info("Initializing update timer...");
        updateTimer = new Timer("BMS Update Timer", true);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                getChannel();
            }
        };
        updateTimer.scheduleAtFixedRate(task, (long) 1 * 1000, (long) 1 * 1000);
    }
   
}
