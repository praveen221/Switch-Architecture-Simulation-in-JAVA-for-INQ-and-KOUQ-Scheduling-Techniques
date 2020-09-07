Assignment 2 submission by:
Praveen Jangid 150101048
Bhola Shankar Rathia 150101016

THE CODE CAN BE FOUND IN THE FOLDER assignment2_150101048_150101016 --> src --> assignment2_150101048_150101016.java

The code can be compiled using the command "javac assignment2_150101048_150101016.java"

For running the code use 

java assignment2_150101048_150101016 no_of_ports buffer_size packet_generation_probability packet_scheduling_method K outputfilename.txt

example
FOR INQ 
note : for inq the value of k is insignificant but is in the middle of command inputs, so please enter .6 or any other factor between 0-1. 

java assignment2_150101048_150101016 8 4 .5 INQ .6 example.txt


FOR KOUQ
note : here the value of k plays an important role and should be entered as factor which is k = factor * N, where N is number of ports

java assignment2_150101048_150101016 8 4 .4 KOUQ .8 exmaple.txt


The output will be generated in the suggested format but will also include dropped proabbility in case of INQ as well, As same print fucntion is used for both. 

The appended file which will be generated inside the folder assignment2_150101048_150101026 --> src -- > example.txt

  


