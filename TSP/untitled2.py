# -*- coding: utf-8 -*-
"""
Created on Sun Feb 21 15:27:02 2021

@author: 1700003918
"""
import pandas as pd
import math
import numpy as np
import matplotlib.pyplot as plt
#from filterpy.kalman import KalmanFilter

filtreli=[]
ham=[]
filtrelix=[]
filtreliy=[]
hamx=[]
hamy=[]
f = open("divide_tsp_600.txt","r")
hm=f.readlines()
hm=hm[0]
ss=len(hm[0])
hm = hm.split(",")
for i in range(len(hm)):
    if i%2==0:
        hamx.append(float(hm[i]))
    if i%2==1:
        hamy.append(float(hm[i]))
plt
fig = plt.figure()

plt.plot(hamy,hamx,".-")
plt.plot(hamy[0],hamx[0],"r.")
plt.legend(["ham veri","filtreli veri"])
f.close()
a=0
b=0
"""
for i in range(105143):
    if i%4==2:
        
        filtreli.append(hm[i][:19])
        filtreliy.append(float(filtreli[a][10:]))
        filtrelix.append(float(filtreli[a][:9]))
        a=a+1
    if i%4==3:
        ham.append(hm[i][:15])
        hamy.append(float(ham[b][8:]))
        hamx.append(float(ham[b][:7]))
        b=b+1

filtrelix=np.array(filtrelix)
filtreliy=np.array(filtreliy)
farkx=hamx[0]-filtrelix[0]   
farky=hamy[0]-filtreliy[0]
filtrelix=filtrelix+(farkx+0.02)
filtreliy=filtreliy+(farky-0.006)
plt
fig = plt.figure()
plt.plot(hamx,hamy,".")
plt.plot(filtrelix,filtreliy)
plt.legend(["ham veri","filtreli veri"])

"""