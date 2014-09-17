#include <cmqc.h>
#include <stdio.h>

MQHCONN  hcon;                    /* connection handle         */
MQHOBJ   hobjg;                   /* object handle             */
MQHOBJ   hobjp;                   /* object handle             */
MQMD     mdg = {MQMD_DEFAULT};    /* Message Descriptor        */
MQMD     mdp = {MQMD_DEFAULT};    /* Message Descriptor        */


main()
{

	MQOD     odg = {MQOD_DEFAULT};
	MQOD     odp = {MQOD_DEFAULT};

	MQLONG   o_options;
	MQLONG   compcode;
	MQLONG   reason;


	char SimQM[50];
	sprintf(SimQM,"QM_BK_BI10");
	MQCONN(	SimQM, &hcon, &compcode, &reason); 
	if (compcode == MQCC_FAILED)
	{
		printf("MQCONN ended with reason code %ld\n", reason);
		return(-1);
	}

	MQDISC(&hcon, &compcode, &reason);
	if (compcode == MQCC_FAILED)
	{
		printf("MQCONN ended with reason code %ld\n", reason);
		return(-1);
	}



}
