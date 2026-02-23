
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
