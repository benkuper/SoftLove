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

        OSCClient client;

        public MainPage()
        {
            this.InitializeComponent();
            this.DataContext = this.viewModel = App.Current;

            this.viewModel.StatusMessage = "Test";

            client = new OSCClient("127.0.0.1", "7776", true);
        }

        

        private async void Button_Click(object sender, RoutedEventArgs e)
        {
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

                        while(true)
                        {
                            await bandClient.SensorManager.HeartRate.StartReadingsAsync();

                            // Receive HeartRate data for a while, then stop the subscription.
                            await Task.Delay(TimeSpan.FromSeconds(1));
                            await bandClient.SensorManager.HeartRate.StopReadingsAsync();

                            this.viewModel.StatusMessage = string.Format("Done. {0} Accelerometer samples were received, final HR = {1}", samplesReceived,heartRate);
                            OSCMessage msg = new OSCMessage("/emotion/heart");
                            msg.Append<int>(heartRate);
                            client.Send(msg);
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                this.viewModel.StatusMessage = ex.ToString();
            }
        }

        private void textBlock_SelectionChanged(object sender, RoutedEventArgs e)
        {

        }
    }
}
