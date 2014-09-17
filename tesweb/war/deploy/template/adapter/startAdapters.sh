cp=$(awk 'BEGIN{while("ls | grep .jar" | getline d) printf":%s",d}')
java -cp .$cp com.dc.tes.adapter.framework.StartUp