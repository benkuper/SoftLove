﻿using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices.WindowsRuntime;
using Windows.Foundation;
using Windows.Foundation.Collections;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Primitives;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Input;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Navigation;

using Microsoft.Band;
using System.Threading.Tasks;

using UnityOSC;
using System.Net;
using Windows.Networking.Sockets;
using Windows.Storage;


// Pour en savoir plus sur le modèle d'élément Page vierge, consultez la page http://go.microsoft.com/fwlink/?LinkId=234238

namespace TestWin8Band
{
    /// <summary>
    /// Une page vide peut être utilisée seule ou constituer une page de destination au sein d'un frame.
    /// </summary>
    public sealed partial class MainPage : Page
    {
        private App viewModel;

        private int heartRate = 0;
        private int gsr = 0;

        OSCClient client;
        String ip;
        String port;

        StorageFile file;
        public MainPage()
        {
            this.InitializeComponent();
            this.DataContext = this.viewModel = App.Current;

            this.viewModel.StatusMessage = "Test";

            ip = "127.0.0.1";
            port = "7776";
        }

        private void updateClient()
        {
            if (client != null) client.Close();
            client = new OSCClient(ip, port, true);

            this.viewModel.StatusMessage = string.Format("Client set to {0}:{1}\n", ip, port);
        }

        private async void Button_Click(object sender, RoutedEventArgs e)
        {

            updateClient();
            this.viewModel.StatusMessage = "Running ...";

            try
            {
                // Get the list of Microsoft Bands paired to the phone.
                IBandInfo[] pairedBands = await BandClientManager.Instance.GetBandsAsync();
                if (pairedBands.Length < 1)
                {
                    this.viewModel.StatusMessage = "This sample app requires a Microsoft Band paired to your device. Also make sure that you have the latest firmware installed on your Band, as provided by the latest Microsoft Health app.";
                    return;
                }

               
                // Connect to Microsoft Band.
                using (IBandClient bandClient = await BandClientManager.Instance.ConnectAsync(pairedBands[0]))
                {

                    bool heartRateConsentGranted;

                    // Check whether the user has granted access to the HeartRate sensor.
                    if (bandClient.SensorManager.HeartRate.GetCurrentUserConsent() == UserConsent.Granted)
                    {
                        heartRateConsentGranted = true;
                    }
                    else
                    {
                        heartRateConsentGranted = await bandClient.SensorManager.HeartRate.RequestUserConsentAsync();
                    }

                    if (!heartRateConsentGranted)
                    {
                        this.viewModel.StatusMessage = "Access to the heart rate sensor is denied.";
                    }
                    else
                    {
                        int samplesReceived = 0; // the number of Accelerometer samples received

                        // Subscribe to Accelerometer data.
                        bandClient.SensorManager.HeartRate.ReadingChanged += (s, args) =>
                        {
                            
                            heartRate = args.SensorReading.HeartRate;
                            samplesReceived++;

                        };


                        bool gsrOK = false;
                        if (bandClient.SensorManager.Gsr.IsSupported)
                        {

                            bandClient.SensorManager.Gsr.ReadingChanged += (s, args) =>
                            {
                                gsr = args.SensorReading.Resistance;

                            };
                            gsrOK = true;
                        }
                        else
                        {
                            this.viewModel.StatusMessage = "Gsr sensor is not supported with your Band version. Microsoft Band 2 is required.";
                        }
                       

                        while (true)
                        {
                            await bandClient.SensorManager.HeartRate.StartReadingsAsync();
                            if(gsrOK) await bandClient.SensorManager.Gsr.StartReadingsAsync();

                            await Task.Delay(TimeSpan.FromSeconds(1));

                            await bandClient.SensorManager.HeartRate.StopReadingsAsync();
                            if(gsrOK) await bandClient.SensorManager.Gsr.StopReadingsAsync();

                            this.viewModel.StatusMessage = string.Format("Sending to {0}:{1}\nHeart Rate = {2}\nGSR = {3}",client.ClientIPAddress,client.Port, heartRate,gsr);

                            OSCMessage msg = new OSCMessage("/emotion/heart");
                            msg.Append<int>(heartRate);
                            client.Send(msg);

                            if (gsrOK)
                            {
                                OSCMessage msg2 = new OSCMessage("/emotion/gsr");
                                msg2.Append<int>(gsr);
                                client.Send(msg2);
                            }
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                this.viewModel.StatusMessage = ex.StackTrace.ToString();
            }
        }

        private void ipTF_TextChanged(object sender, TextChangedEventArgs e)
        {
            ip = ipTF.Text;
        }

        private void portTF_TextChanged(object sender, TextChangedEventArgs e)
        {
            port = portTF.Text;
       }

        private void setClientBT_Click(object sender, RoutedEventArgs e)
        {
            updateClient();
        }
    }
}
