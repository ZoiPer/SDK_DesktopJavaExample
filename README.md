# Before you start

## Purpose of this documentation

This guide assists you in rapidly developing your VoIP application with Zoiper SDK 2.0. This manual contains an overview of the entities in the SDK with a lot of practical examples of implementation, usage and configuration.

**By default zdk.net is not included in the example. If you still don't have the zdk.net library, please contact us on zoiper.com.**

## Licensing

To enjoy the powerful benefits of Zoiper SDK 2.0, you need a license. Depending on your needs, you can buy 2 different types of licenses:

- Installation license per end-user
- Unlimited installations;

Please [<span style="color:orange">contact Zoiper</span>](mailto:sales@zoiper.com) for more details and test licenses or to receive licenses for testing purposes.

## Threading model

Zoiper SDK 2.0 is thread-safe. Shared objects can be called simultaneously from multiple threads. All callbacks from the SDK modules to the application code are performed in the context of the application thread which invokes the respective functions and methods.

In order to receive callbacks, the SDK needs to receive processing time from your application core. You can achieve this by invoking the respective functions.

On Android, iOS, and macOS, the main UI thread usually handles the assignment of processing time to the SDK.

Regarding sockets and transports, the SDK manages and utilizes the threads internally. For the interaction with sockets and transports, the SDK also internally manages and utilizes its own separate thread. As a result, the application code can use the processing time without blocking the SDK sockets.

## More resources

Inside the SDK packages, you can find the respective reference and examples of basic usage for all:

- methods
- functions
- APIs
- callbacks
- etc.

## Third-party software

The SDK is (partially) built with:

- JThread, Copyright (C) 2000-2005 Jori Liesenborgs (jori@lumumba.uhasselt.be)
- JRTPLIB, part of JRTPLIB Copyright (C) 1999-2005 Jori Liesenborgs
- GSM, Copyright 1992, 1993, 1994 by Jutta Degener and Carsten Bormann, Technische Universitaet Berlin
- iLBC, iLBC Speech Coder ANSI-C Source Code iLBC_define.h Copyright (C) The Internet Society (2004). All Rights Reserved.
- SPEEX The Xiph OSC and the Speex Parrot logos are trademarks (TM) of Xiph.Org.
- OpenLDAP, Copyright 1999-2003 The OpenLDAP Foundation, Redwood City, California, USA. All Rights Reserved.
- PortAudio, Copyright (C) 1999-2002 Ross Bencina and Phil Burk
- PortMixer, PortMixer, Windows WMME Implementation, Copyright (C) 2002, Written by Dominic Mazzoni and Augustus Saunders
- Resiprocate, The Vovida license The Vovida Software License, Version 1.0, Copyright (C) 2000 Vovida Networks, Inc. All rights reserved.
- This product includes cryptographic software written by Eric Young (eay@cryptsoft.com).
- This product includes software written by Tim Hudson (tjh@cryptsoft.com).
- This product is using the gloox XMPP library - Copyright by Jakob Schroeter.

Please contact [<span style="color:orange">sales@zoiper.com</span>](mailto:sales@zoiper.com) for more information.