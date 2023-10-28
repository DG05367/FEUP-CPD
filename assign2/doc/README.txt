To compile the files run the compile.sh script
We left some scripts to run some tests more easily
Still we will leave here some info:

Run STORE   java store.Store 224.0.0.1 3030 127.0.0.1 3000

Run CLIENT  java client.TestClient 127.0.0.1:3000 get <file_key> or
    	    java client.TestClient 127.0.0.1:MService join

MService is the name of the membership service.
rmiregistry is opened by store.

There are no premade tests for 'GET' since we could not finish it in time.