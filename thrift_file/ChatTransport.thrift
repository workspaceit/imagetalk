namespace java socket.thrift_service
namespace cocoa thriftService

/**
 * Ahh, now onto the cool part, defining a service. Services just need a name
 * and can optionally inherit from another service using the extends keyword.
 */
 struct ResponseObj{
     1:optional string msg;
     2:required bool status = true;
 }
 
service ChatTransport {


   string getToken(1:string accessToken),
   ResponseObj sendVideo(1:i32 appCredentialId,2:string token,3:string socketResponse,4:binary bufferedByte,5:string fileName),
   ResponseObj sendPicture(1:i32 appCredentialId,2:string token,3:string socketResponse,4:binary bufferedByte,5:string fileName),
   ResponseObj sendVoice(1:i32 appCredentialId,2:string token,3:string socketResponse,4:binary bufferedByte,5:string fileName),
   ResponseObj expireMyToken(1:i32 appCredentialId,2:string token)


}
