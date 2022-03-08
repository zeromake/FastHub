import math

def colour_distance(rgb_1, rgb_2):
    R_1,G_1,B_1 = rgb_1
    R_2,G_2,B_2 = rgb_2
    rmean = (R_1 +R_2 ) / 2
    R = R_1 - R_2
    G = G_1 -G_2
    B = B_1 - B_2
    return math.sqrt((2+rmean/256)*(R**2)+4*(G**2)+(2+(255-rmean)/256)*(B**2))


