# fault_replicate


##  Fault Replication(F22) 

###Symptom：
When the user switch to Flow Three - Consign & Voucher 
and click the button of "Print Voucher" if any,
the result page displays "Empty. No data!".

###Root Cause
The constructed SQL statement includes a wrong column name
in the “select” part according to its “from” part

