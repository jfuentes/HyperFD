#!/bin/bash
# -*- ENCODING: UTF-8 -*-
command clear
echo '---------------------Inicio del Script---------------------'
#Alias de compilación
compilar="javac -d $HOME/HyperFD/classes -classpath $HOME/HyperFD/classes:$HOME/HyperFD/lib/javacsv.jar:$HOME/HyperFD/lib/ga-frame.jar -encoding MacRoman -sourcepath $HOME/HyperFD/src -g -Xlint:all -Xlint:-cast -Xlint:-empty -Xlint:-fallthrough -Xlint:-path -Xlint:-processing -Xlint:-serial -Xlint:-unchecked $HOME/HyperFD/src/refutacion/RowX.java $HOME/HyperFD/src/refutacion/TuplaCheck.java $HOME/HyperFD/src/refutacion/Antecedente.java $HOME/HyperFD/src/refutacion/Main.java $HOME/HyperFD/src/utils/IteradorCombinacion.java $HOME/HyperFD/src/refutacion/Hipergrafo.java $HOME/HyperFD/src/refutacion/Refutacion.java $HOME/HyperFD/src/refutacion/Prueba.java $HOME/HyperFD/src/refutacion/Kavvadias.java $HOME/HyperFD/src/refutacion/Consecuente.java"
#Alias de ejecución
ejecutar="java -client -classpath $HOME/.adf:$HOME/HyperFD/classes:$HOME/HyperFD/lib/javacsv.jar:$HOME/HyperFD/lib/ga-frame.jar' refutacion.Main MURAKAMI "
echo 'Compilando...'
$compilar
echo -e 'Ejecutando...\n\n'

echo "___________________________________________________________"
echo "--- Column Test -------------------------------------------"
echo "Column Test (2369 Tuplas)" >> $HOME/HyperFD/logs
ejecutar2="$ejecutar ad_0016x2369.csv 16 2369"; $ejecutar2;
ejecutar2="$ejecutar ad_0032x2369.csv 32 2369"; $ejecutar2;
ejecutar2="$ejecutar ad_0064x2369.csv 64 2369"; $ejecutar2;
ejecutar2="$ejecutar ad_0128x2369.csv 128 2369"; $ejecutar2;
ejecutar2="$ejecutar ad_0256x2369.csv 256 2369"; $ejecutar2;
ejecutar2="$ejecutar ad_0384x2369.csv 384 2369"; $ejecutar2;
ejecutar2="$ejecutar ad_0512x2369.csv 512 2369"; $ejecutar2;
ejecutar2="$ejecutar ad_0640x2369.csv 640 2369"; $ejecutar2;
ejecutar2="$ejecutar ad_0768x2369.csv 768 2369"; $ejecutar2;
ejecutar2="$ejecutar ad_0832x2369.csv 832 2369"; $ejecutar2;
ejecutar2="$ejecutar ad_0896x2369.csv 896 2369"; $ejecutar2;
ejecutar2="$ejecutar ad_1024x2369.csv 1024 2369"; $ejecutar2;

echo "___________________________________________________________"
echo "--- Row Test ----------------------------------------------"
echo "
Row Test (10 Atributos)" >> $HOME/HyperFD/logs
ejecutar2="$ejecutar susy_10x0000512.csv 10 512"; $ejecutar2;
ejecutar2="$ejecutar susy_10x0001024.csv 10 1024"; $ejecutar2;
ejecutar2="$ejecutar susy_10x0002048.csv 10 2048"; $ejecutar2;
ejecutar2="$ejecutar susy_10x0004096.csv 10 4096"; $ejecutar2;
ejecutar2="$ejecutar susy_10x0008192.csv 10 8192"; $ejecutar2;
ejecutar2="$ejecutar susy_10x0016384.csv 10 16384"; $ejecutar2;
ejecutar2="$ejecutar susy_10x0032768.csv 10 32768"; $ejecutar2;
ejecutar2="$ejecutar susy_10x0065536.csv 10 65536"; $ejecutar2;
ejecutar2="$ejecutar susy_10x0131072.csv 10 131072"; $ejecutar2;

#Tiempo Inaceptable
#ejecutar2="$ejecutar susy_10x0262144.csv 10 262144"; $ejecutar2;
#ejecutar2="$ejecutar susy_10x0393216.csv 10 393216"; $ejecutar2;
#ejecutar2="$ejecutar susy_10x0524288.csv 10 524288"; $ejecutar2;
#ejecutar2="$ejecutar susy_10x0655360.csv 10 655360"; $ejecutar2;
#ejecutar2="$ejecutar susy_10x0786432.csv 10 786432"; $ejecutar2;
#ejecutar2="$ejecutar susy_10x0917504.csv 10 917504"; $ejecutar2;
#ejecutar2="$ejecutar susy_10x1048576.csv 10 1048576"; $ejecutar2;
echo "------------------------------------------------
" >> $HOME/HyperFD/logs
echo -e '\n\n----------------------Fin del Script-----------------------'
exit