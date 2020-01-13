
Copiar el JAR en el directorio que se desee ejecutarlo.
Abrir un consola (en windows CMD) ejecutar en el directorio que esta el .jar el siguiente comando, 
indicando luego del jar como primer paremetro el site_id y luego los seller_id que se quieran consultar.

Ejemplo
java -jar LogInfoItemsBySeller-v1.0.jar MLA 179571326

En este caso el site_id es MLA y el seller_id buscado es el 179571326

Como resultado de la corrida se generar el directorio output con los archivos resultantes (uno por cada seller_id pasado como parametro). 

