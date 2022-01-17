// RFID.h : main header file for the RFID DLL
//

#define RFID_EXPORTS

#ifdef	RFID_EXPORTS
#define RFID_API __declspec(dllexport) __stdcall
#else
#define RFID_API __declspec(dllimport) __stdcall
#endif


// System Command Function
extern "C" int		RFID_API API_GetSysComm(unsigned char *Buffer);

extern "C" HANDLE	RFID_API API_OpenComm(int nCom, int nBaudrate);

extern "C" BOOL		RFID_API API_CloseComm(HANDLE commHandle);

extern "C" int		RFID_API API_SetDeviceAddress(HANDLE commHandle, int DeviceAddress, unsigned char NewAddr, unsigned char *Buffer);

extern "C" int		RFID_API API_SetBaudrate(HANDLE commHandle, int DeviceAddress, unsigned char NewBaud, unsigned char *Buffer);

extern "C" int		RFID_API API_SetSerNum(HANDLE commHandle, int DeviceAddress, unsigned char *NewValue, unsigned char *Buffer);

extern "C" int		RFID_API API_GetSerNum(HANDLE commHandle, int DeviceAddress, unsigned char *Buffer);

//extern "C" int	RFID_API API_WriteUserInfo(HANDLE commHandle, int DeviceAddress, int NumBlockk, int NumLength, char *UserInfo);

//extern "C" int	RFID_API API_ReadUserInfo(HANDLE commHandle, int DeviceAddress, int NumBlock, int NumLength, char *UserInfo);

extern "C" int		RFID_API API_GetVersionNum(HANDLE commHandle, int DeviceAddress, char *VersionNum);

extern "C" int		RFID_API API_ControlLED(HANDLE commHandle, int DeviceAddress, unsigned char Freq, unsigned char Duration, unsigned char *Buffer);

extern "C" int		RFID_API API_ControlBuzzer(HANDLE commHandle, int DeviceAddress, unsigned char Freq, unsigned char Duration, unsigned char *Buffer);


// ISO14443 TypeA Function
extern "C" int RFID_API API_MF_Request(HANDLE commHandle, int DeviceAddress, unsigned char inf_mode, unsigned char *Buffer);

extern "C" int RFID_API API_MF_Anticoll(HANDLE commHandle, int DeviceAddress, unsigned char *Buffer);

extern "C" int RFID_API API_MF_Select(HANDLE commHandle, int DeviceAddress, unsigned char UIDLen, unsigned char *uid, unsigned char *Buffer);

extern "C" int RFID_API API_MF_Halt(HANDLE commHandle, int DeviceAddress);


// ISO14443 TypeB Function
extern "C" int RFID_API API_Request_B(HANDLE commHandle, int DeviceAddress, unsigned char *Buffer);

extern "C" int RFID_API API_Anticoll_B(HANDLE commHandle, int DeviceAddress, unsigned char *Buffer);

extern "C" int RFID_API API_Attrib_B(HANDLE commHandle, int DeviceAddress, unsigned char *SerialNum, unsigned char *Buffer);

extern "C" int RFID_API API_RESET_B(HANDLE commHandle, int DeviceAddress, unsigned char *Buffer);

extern "C" int RFID_API API_TransferCMD_B(HANDLE commHandle, int DeviceAddress, unsigned char cmdSize, unsigned char *cmd, unsigned char *Buffer);


// ISO15693 Function
extern "C" int RFID_API API_ISO15693_Inventory(	HANDLE commHandle, int deviceAddress, unsigned char flag, 
												unsigned char afi, unsigned char datalen, const unsigned char *pData,
												unsigned char *pBuffer);

extern "C" int RFID_API API_ISO15693_Read(	HANDLE commHandle, int DeviceAddress, unsigned char flags,
											unsigned char blk_add, unsigned char num_blk,
                                            unsigned char *uid, unsigned char *buffer);

extern "C" int RFID_API API_ISO15693_Write(	HANDLE commHandle, int DeviceAddress, unsigned char flags,
                                            unsigned char blk_add, unsigned char num_blk,
                                            unsigned char *uid, unsigned char *data);

extern "C" int RFID_API API_ISO15693_Lock(	HANDLE commHandle, int DeviceAddress, unsigned char flags,
                                            unsigned char num_blk, unsigned char *uid, unsigned char  *buffer);

extern "C" int RFID_API API_ISO15693_StayQuiet(	HANDLE commHandle, int DeviceAddress, unsigned char flags,
                                                unsigned char *uid,  unsigned char  *buffer );

extern "C" int RFID_API API_ISO15693_Select(HANDLE commHandle, int DeviceAddress, unsigned char flags,
											unsigned char *uid,  unsigned char  *buffer );

extern "C" int RFID_API API_ISO15693_ResetToReady(	HANDLE commHandle, int DeviceAddress, unsigned char flags,
													unsigned char *uid,  unsigned char *buffer );

extern "C" int RFID_API API_ISO15693_WriteAFI(	HANDLE commHandle, int DeviceAddress, unsigned char flags,
												unsigned char afi, unsigned char *uid, unsigned char *buffer);

extern "C" int RFID_API API_ISO15693_LockAFI(	HANDLE commHandle, int DeviceAddress, unsigned char flags,
												unsigned char *uid, unsigned char *buffer );

extern "C" int RFID_API API_ISO15693_WriteDSFID(HANDLE commHandle, int DeviceAddress, unsigned char flags,
												unsigned char DSFID, unsigned char *uid, unsigned char *buffer);

extern "C" int RFID_API API_ISO15693_LockDSFID(	HANDLE commHandle, int DeviceAddress, unsigned char flags,
												unsigned char *uid, unsigned char *buffer );

extern "C" int RFID_API API_ISO15693_GetSysInfo(HANDLE commHandle, int deviceAddress,
												unsigned char flag, unsigned char *uid, unsigned char *buffer);

extern "C" int RFID_API API_ISO15693_GetMulSecurity(HANDLE commHandle, int deviceAddress, unsigned char flag,
													unsigned char blkAddr, unsigned char blkNum, const unsigned char *uid,
													unsigned char *pBuffer);

extern "C" int RFID_API API_ISO15693_TransCmd(	HANDLE commHandle, int DeviceAddress, int cmdSize, unsigned char *cmd,
												unsigned char *pbuffer);


// Mifare Application Function
extern "C" int RFID_API API_MF_Read(HANDLE commHandle, int DeviceAddress, unsigned char mode, unsigned char blk_add, 
									unsigned char num_blk, unsigned char *key, unsigned char *Buffer);

extern "C" int RFID_API API_MF_Write(HANDLE commHandle, int DeviceAddress, unsigned char mode, unsigned char blk_add, 
									 unsigned char num_blk, unsigned char *key, unsigned char *senddata, unsigned char *Buffer);

extern "C" int RFID_API API_MF_InitVal(HANDLE commHandle, int DeviceAddress, unsigned char mode, unsigned char sec_num, 
									   unsigned char *key, unsigned char *value, unsigned char *Buffer);

extern "C" int RFID_API API_MF_Dec(	HANDLE commHandle, int DeviceAddress, unsigned char mode, unsigned char sec_num,
									unsigned char *key, unsigned char *value, unsigned char *Buffer);

extern "C" int RFID_API API_MF_Inc(	HANDLE commHandle, int DeviceAddress, unsigned char mode, unsigned char sec_num,
									unsigned char *key, unsigned char *value, unsigned char *Buffer);

extern "C" int RFID_API API_MF_GET_SNR(HANDLE commHandle, int DeviceAddress, unsigned char mode, unsigned char cmd,
									   unsigned char *Buffer);

//extern "C" int RFID_API API_MF_Value(HANDLE commHandle, int DeviceAddress, unsigned char mode, unsigned char add_blk, int value);


// CPU Card Function
extern "C" int RFID_API API_MF_PowerOn(HANDLE commHandle, int DeviceAddress, unsigned char mode, unsigned char cmd, unsigned char *Buffer);

extern "C" int RFID_API API_MF_TransferCMD(HANDLE commHandle, int DeviceAddress, unsigned char mode, unsigned char cmdlength,
										   unsigned char *cmd, unsigned char *Buffer);

extern "C" int RFID_API API_MF_RST_Antenna(HANDLE commHandle, int DeviceAddress, unsigned char *Buffer);

