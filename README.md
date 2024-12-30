# Mangia e basta | Kotlin

## Overview
App that delivers food by drones. Menu can be ordered from a list of closeby restaurants. 

### Useful links: 
- [API Documentation](https://develop.ewlab.di.unimi.it/mc/2425/)
- [Project document](https://myariel.unimi.it/pluginfile.php/260624/mod_resource/content/1/Progetto%202425%20V1.pdf)

## Epics
### EP-01: MENU: User can see a list of the menus at flight distance, every menu has:
   - Name
   - Image
   - Cost
   - Description
   - ETA
   
   By clicking on any menu of the list user can see the details of the menu.
    - Same info as above but image is bigger
    - Longer description
    - Button to order

    Limitations:
    - User can't order more if there's a pending order.
    - User can't order if he did not complete all the profile info.

### EP-02: PROFILE: User can see and modify his own info in a profile page.
   - Name, surname
   - Credit card info
     - Name Surname (only one field)
     - Card number
     - Expiry date
     - CVV
   - Last order info (see point 3)
   
    Name and Surname are max 15 chars. Credit card number is 31 char max.


### EP-03: ORDER: After ordering the menu it appears in the user info page (and right after placing order).
   - Name
   - Status (delivering | delivered)
   - ETA (TA for already delivered orders)
   - Map 
     - for delivering orders: 
       - restaurant position where drone lifted
       - drone position
       - destination position
     - for delivered orders:
       - restaurant position
       - destination position
   - ETA
   - Status (pending, in progress, delivered)
   - Button to cancel order

## Technical details
- [ ] First app launch asks for SID and stores it locally to use in every server call.
- [ ] Order page should be updates automatically every 5 seconds.
- [x] Application should keep last visited page open when user goes back to it.
- [ ] All images are square and Base64 (no html prefix)
- [x] Pixel 7 API 31

### Client-Server sequence
[![client-server sequence diagram](https://mermaid.ink/img/pako:eNp9UT1PwzAQ_SunWxhIq6ZJm9ZDByhIHbpQJpTFjY_UamwH20ENUf47TgNIDHDT3bv3Ifs6LIwgZOjorSFd0Fby0nKVawh1X0nSfrLZ3B7IvpNl8Cit83A0xkegiQQcdtuROzImgTwZZQyeT2QJWtNAaf4y3N0oKHhVSV0C10Ba1Ebq4D5obxyo9t-IJ3K10Y7gyIszcAf-SxdS7aDECBVZxaUIj-wGnxwDR1GOLLSC23OO0YgXFXduwLsxL8eTFPSgat_uSR3JujtzGfbeNjRQ-lz3IYA33hxaXSAbFhFa05QnZK-8cmFqasH997_-oDXXL8b8mpF1eEG2nE_XWbZaJkkcZ8tssYqwRRYnyTSdrdNFmqSLbJbE8z7Cj6tDHCEJ6Y3dj6e8XrT_BHs2l2E?type=png)](https://mermaid.live/edit#pako:eNp9UT1PwzAQ_SunWxhIq6ZJm9ZDByhIHbpQJpTFjY_UamwH20ENUf47TgNIDHDT3bv3Ifs6LIwgZOjorSFd0Fby0nKVawh1X0nSfrLZ3B7IvpNl8Cit83A0xkegiQQcdtuROzImgTwZZQyeT2QJWtNAaf4y3N0oKHhVSV0C10Ba1Ebq4D5obxyo9t-IJ3K10Y7gyIszcAf-SxdS7aDECBVZxaUIj-wGnxwDR1GOLLSC23OO0YgXFXduwLsxL8eTFPSgat_uSR3JujtzGfbeNjRQ-lz3IYA33hxaXSAbFhFa05QnZK-8cmFqasH997_-oDXXL8b8mpF1eEG2nE_XWbZaJkkcZ8tssYqwRRYnyTSdrdNFmqSLbJbE8z7Cj6tDHCEJ6Y3dj6e8XrT_B1Hs2l2E)

###