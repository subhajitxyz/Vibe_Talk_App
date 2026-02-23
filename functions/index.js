/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

// const {setGlobalOptions} = require("firebase-functions");
// const {onRequest} = require("firebase-functions/https");
// const logger = require("firebase-functions/logger");

// For cost control, you can set the maximum number of containers that can be
// running at the same time. This helps mitigate the impact of unexpected
// traffic spikes by instead downgrading performance. This limit is a
// per-function limit. You can override the limit for each function using the
// `maxInstances` option in the function's options, e.g.
// `onRequest({ maxInstances: 5 }, (req, res) => { ... })`.
// NOTE: setGlobalOptions does not apply to functions using the v1 API. V1
// functions should each use functions.runWith({ maxInstances: 10 }) instead.
// In the v1 API, each function can only serve one request per container, so
// this will be the maximum concurrent request count.
// setGlobalOptions({ maxInstances: 10 });

// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

// const functions = require("firebase-functions");
// const admin = require("firebase-admin");
//
// admin.initializeApp();
//
// exports.sendMessageNotification = functions.firestore
//    .document("chats/{chatRoomId}/lastMessage")
//    .onCreate(async (snapshot, context) => {
//      const message = snapshot.data();
//      const chatRoomId = context.params.chatRoomId;
//
//      if (!message) return null;
//
//      const senderId = message.senderId;
//      const senderName = message.senderName;
//      const text = message.body || "New message";
//
//      const chatRoomDoc = await admin
//          .firestore()
//          .collection("chats")
//          .doc(chatRoomId)
//          .get();
//
//      if (!chatRoomDoc.exists) return null;
//
//      const chatRoomData = chatRoomDoc.data();
//      const participants = chatRoomData.participants;
//
//      if (!participants || participants.length === 0) return null;
//
//      const receivers = participants.filter(
//          (id) => id !== senderId,
//      );
//
//      // Fetch all users in parallel
//      const userDocs = await Promise.all(
//          receivers.map((id) =>
//            admin.firestore().collection("users").doc(id).get(),
//          ),
//      );
//
//      for (const userDoc of userDocs) {
//        if (!userDoc.exists) continue;
//
//        const userData = userDoc.data();
//
//        if (
//          !userData.fcmTokens ||
//        userData.fcmTokens.length === 0
//        ) {
//          continue;
//        }
//
//        const payload = {
//          data: {
//            title: "New Message",
//            body: text,
//            chatRoomId,
//            senderId,
//            senderName,
//            type: "chat",
//          },
//        };
//
//        await admin.messaging().sendMulticast({
//          tokens: userData.fcmTokens,
//          data: payload.data,
//        });
//      }
//
//      return null;
//    });


// const {onDocumentCreated} = require("firebase-functions/v2/firestore");
// const admin = require("firebase-admin");
//
// admin.initializeApp();

// exports.sendMessageNotification = onDocumentCreated(
//    "chats/{chatRoomId}/lastMessage",
//    async (event) => {
//      const snapshot = event.data;
//      if (!snapshot) return;
//
//      const message = snapshot.data();
//      const chatRoomId = event.params.chatRoomId;
//
//      if (!message) return;
//
//      const senderId = message.senderId;
//      const senderName = message.senderName;
//      const text = message.body || "New message";
//
//      const chatRoomDoc = await admin
//          .firestore()
//          .collection("chats")
//          .doc(chatRoomId)
//          .get();
//
//      if (!chatRoomDoc.exists) return;
//
//      const chatRoomData = chatRoomDoc.data();
//      const participants = chatRoomData.participants;
//
//      if (!participants || participants.length === 0) return;
//
//      const receivers = participants.filter(
//          (id) => id !== senderId,
//      );
//
//      const userDocs = await Promise.all(
//          receivers.map((id) =>
//            admin.firestore().collection("users").doc(id).get(),
//          ),
//      );
//
//      for (const userDoc of userDocs) {
//        if (!userDoc.exists) continue;
//
//        const userData = userDoc.data();
//
//        if (
//          !userData.fcmTokens ||
//        userData.fcmTokens.length === 0
//        ) {
//          continue;
//        }
//
//        await admin.messaging().sendMulticast({
//          tokens: userData.fcmTokens,
//          data: {
//            title: "New Message",
//            body: text,
//            chatRoomId,
//            senderId,
//            senderName,
//            type: "chat",
//          },
//        });
//      }
//    },
// );


const {onDocumentCreated} = require("firebase-functions/v2/firestore");
const admin = require("firebase-admin");

admin.initializeApp();

exports.sendMessageNotification = onDocumentCreated(
    {
      document: "chats/{chatRoomId}/messages/{messageId}",
      region: "asia-south1", // change if different
    },
    async (event) => {
      const snapshot = event.data;
      if (!snapshot) return;

      const message = snapshot.data();
      const chatRoomId = event.params.chatRoomId;

      const senderId = message.senderId;
      const senderName = message.senderName;
      const text = message.body || "New message";

      // Get chatRoom document to fetch participants
      const chatRoomDoc = await admin
          .firestore()
          .collection("chats")
          .doc(chatRoomId)
          .get();

      if (!chatRoomDoc.exists) return;

      const participants = chatRoomDoc.data().participants || [];

      const receivers = participants.filter((id) => id !== senderId);

      // Fetch user tokens
      const userDocs = await Promise.all(
          receivers.map((id) =>
            admin.firestore().collection("users").doc(id).get(),
          ),
      );

      for (const userDoc of userDocs) {
        if (!userDoc.exists) continue;

        const userData = userDoc.data();
        const tokens = userData && userData.fcmTokens;

        if (!tokens || tokens.length === 0) {
          continue;
        }

        await admin.messaging().sendEachForMulticast({
          tokens,
          android: {
            priority: "high",
          },
          data: {
            senderName: String(senderName),
            body: String(text),
            chatRoomId: String(chatRoomId),
            senderId: String(senderId),
            type: "chat",
          },
        });
      }
    },
);
