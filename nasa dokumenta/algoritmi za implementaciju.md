## Confidentiality via Encryption

1.  The sender creates a message.

2.  The sending OpenPGP generates a random number to be used as a
       session key for this message only.

3.  The session key is encrypted using each recipient's public key.
       These "encrypted session keys" start the message.
 
4.  The sending OpenPGP encrypts the message using the session key,
       which forms the remainder of the message.  Note that the message
       is also usually compressed.

5.  The receiving OpenPGP decrypts the session key using the
       recipient's private key.

6.  The receiving OpenPGP decrypts the message using the session key.
       If the message was compressed, it will be decompressed.  
       


## Authentication via Digital Signature

1.  The sender creates a message.

2.  The sending software generates a hash code of the message.

3.  The sending software generates a signature from the hash code
   using the sender's private key.

4.  The binary signature is attached to the message.

5.  The receiving software keeps a copy of the message signature.

6.  The receiving software generates a new hash code for the received
   message and verifies it using the message's signature.  If the
   verification is successful, the message is accepted as authentic.  
   
   
## Compression 

OpenPGP implementations SHOULD compress the message after applying
   the signature but before encryption.
   
   
## Conversion to Radix-64 
Prebacivanje u ascii umesto da ostane u binarnom.  
Nisam sigurna sta se desava sa porukama koje imaju i druge znakove..?  



## Key IDs

A Key ID is an eight-octet scalar that identifies a key.
    Implementations SHOULD NOT assume that Key IDs are unique.   


## Time Fields

   A time field is an unsigned four-octet number containing the number
   of seconds elapsed since midnight, 1 January 1970 UTC.


## Packaet 

U dokumentaciji od 14. strane ima detaljno oobjasnjeno kako da se upakuje paket.
   
   

   

   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
