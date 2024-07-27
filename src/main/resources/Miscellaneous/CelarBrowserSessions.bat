taskkill /F /IM iexplore.exe
taskkill /F /IM IEDriverServer.exe
taskkill /F /IM msedgedriver.exe
taskkill /F /IM msedge.exe
taskkill /F /IM chromedriver.exe
taskkill /F /IM chrome.exe
taskkill /F /IM ElectronApp_chromedriver.exe
taskkill /F /IM geckodriver_x64.exe
taskkill /F /IM geckodriver.exe
taskkill /F /IM firefox.exe
taskkill /F /IM BrowserStackLocal.exe
taskkill /F /IM WinAppDriver.exe
taskkill /F /IM Winium.Desktop.Driver.exe
del /s /f /q %userprofile%\Recent\*.*
del /s /f /q %USERPROFILE%\appdata\local\temp\*.*
rd %temp% /s /q
TIMEOUT 10
:: taskkill /F /IM java.exe
::for /f "tokens=1,2 delims= " %%i in ('jps -m ^| find "selenium-server-standalone"') do ( taskkill /F /PID %%i )

:: for /f "tokens=1,2 delims= " %%i in ('jps -m ^| find "selenium"') do ( taskkill /F /PID %%i )

:: TIMEOUT 10