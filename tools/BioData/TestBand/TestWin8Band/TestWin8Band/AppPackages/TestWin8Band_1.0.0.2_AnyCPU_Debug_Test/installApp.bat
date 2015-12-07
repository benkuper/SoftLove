echo %~dp0
powershell.exe -command "Remove-AppxPackage com.benkuper.bandOSC2_1.0.0.2_neutral__z6xh1x1kg5n3j"
powershell.exe -command "Add-AppxPackage %~dp0\TestWin8Band_1.0.0.2_AnyCPU_Debug.appx" 
pause