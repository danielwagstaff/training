@ECHO OFF
echo Building Order Write Service
cd "Order\Write Service"
echo %CD%
START mvn clean install
cd ..\..\

echo .

echo Building Order Read Service
cd "Order\Read Service"
echo %CD%
START mvn clean install
cd ..\..\

echo .

echo Building Processing Service
cd "Process\Process Service"
echo %CD%
START mvn clean install
cd ..\..\

echo .

echo Building Product Service
cd "Product\Product Service"
echo %CD%
START mvn clean install
cd ..\..\

