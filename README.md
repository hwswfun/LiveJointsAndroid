# LiveJointsAndroid

Real time encouragement for stretching exercises after surgery on a joint

LiveJoints is a combination hardware / software project to provide real time feedback to a patient during a joint therapy session.  This project was started to create a fun way to encourage my daughter to continue her stretching exercises after an injury to her elbow.

Initially, the external hardware fixture will wrap around the arm below and above the elbow.  At the elbow, on one side, there will be a  device integrated that will measure the angle for the patient / therapist to see.  A potentiometer on the other side of the elbow will  allow an arduino or raspberry pi to measure the angle and make it available to software.

<img src="images/contracted-profile.jpg" width="30%"> <img src="images/contracted-android.png" width="30%">

The app software will be run with Berkeley's SNAP! program so it is easy to prototype and so my daughter's brothers can make their own fun games for her.

Other options considered:
* Simply strap augmented reality markers to the upper and lower arm then watch with Unity3d + Qualcomm Vuforia.
 * This would be fairly easy to do but limited to mobile devices only for now since Vuforia is meant for Android / iOS.
* Gyro and accelerometers can be on arm bands attached to the upper and lower arm then the delta of the angle can infer the angle of the elbow.

Long term, it may be nice to make an apparatus that can stay on her arm as part of her clothes so we can get live and accumulated biometrics.  Similar to people wearing pedometers to improve their health throughout the day, having statistics available in real time and throughout the day may allow others to encourage her if the desired activity has not taken place.

Statistics would come in handy for creating the necessary encouragement points.  Machine learning may enable the application to automatically assess abilities and with input from a therapist, chart the appropriate plan.

To be blunt, this project was started because traditional Physical Therapy could benefit by the gamification and machine learning technologies to encourage the patient to reach past their goals.


