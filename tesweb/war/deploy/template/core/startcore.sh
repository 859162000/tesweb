cp=$(awk 'BEGIN{while("ls lib | grep .jar" | getline d) printf":lib/%s",d}')
java -cp .$cp com.dc.tes.fcore.FCore