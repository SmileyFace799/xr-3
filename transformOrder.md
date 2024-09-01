# Proving that transformation order matters
## The 3 transformation matrices used
- **Scaling**, in x, y & z direction (`s_x`, `s_y` & `s_z`)
  - `S(s_x, s_y, s_z)`
- **Translation**, in x, y & z direction (`t_x`, `t_y` & `t_z`)
  - `T(t_x, t_y, t_z)`
- **Rotation**, at an angle (`a`) around an axis (in this case, the z axis, although the same applies for any axis)
  - `Rz(a)`

## Calculating a transformation with these
A transformed point (`p`) can be found by multiplying a transformation matrix (`M`) with the original point (`p_0`).
Finding this total transformation matrix is done by multiplying the individual transformation matrices in transformation order.
Already from this, it is possible to tell that the transformation order matters, due to the rule `AB != BA`,
but for the sake of this task, it will be proven numerically as well.

## Finding the total transformation matrix
Here, two cases of the same transformations in a different order are arbitrarily selected
### Case 1: Translation -> Rotation -> Scaling
```
M_1 = T(t_x, t_y, t_z) * Rz(a) * S(s_x, s_y, s_z)

= |1 0 0 t_x| * |cos(a) -sin(a) 0 0| * |s_x 0   0   0|
= |0 1 0 t_y| * |sin(a) cos(a)  0 0| * |0   s_y 0   0|
= |0 0 1 t_z| * |0      0       1 0| * |0   0   s_z 0|
= |0 0 0 1  | * |0      0       0 1| * |0   0   0   1|

= |cos(a) -sin(a) 0 t_x| * |s_x 0   0   0|
= |sin(a) cos(a)  0 t_y| * |0   s_y 0   0|
= |0      0       1 t_z| * |0   0   s_z 0|
= |0      0       0 1  | * |0   0   0   1|

= |s_x*cos(a) -s_y*sin(a) 0   t_x|
= |s_x*sin(a) s_y*cos(a)  0   t_y|
= |0          0           s_z t_z|
= |0          0           0   1  |
```

### Case 2: Scaling -> Rotation -> Translation

```
M_2 = S(s_x, s_y, s_z) * Rz(a) * T(t_x, t_y, t_z)

= |s_x 0   0   0| * |cos(a) -sin(a) 0 0| * |1 0 0 t_x|
= |0   s_y 0   0| * |sin(a) cos(a)  0 0| * |0 1 0 t_y|
= |0   0   s_z 0| * |0      0       1 0| * |0 0 1 t_z|
= |0   0   0   1| * |0      0       0 1| * |0 0 0 1  |

= |s_x*cos(a) -s_x*sin(a) 0   0| * |1 0 0 t_x|
= |s_y*sin(a) s_y*cos(a)  0   0| * |0 1 0 t_y|
= |0          0           s_z 0| * |0 0 1 t_z|
= |0          0           0   1| * |0 0 0 1  |

= |s_x*cos(a) -s_x*sin(a) 0   t_x*s_x*cos(a)-t_y*s_x*sin(a)|
= |s_y*sin(a) s_y*cos(a)  0   t_x*s_y*sin(a)+t_y*s_y*cos(a)|
= |0          0           s_z t_z*s_z                      |
= |0          0           0   1                            |
```

## Transforming a point

### p<sub>1</sub> = M<sub>1</sub> * p<sub>0</sub>:
```
p_1 = M_1 * p_0

=> |x_1| = |s_x*cos(a) -s_y*sin(a) 0   t_x| * |x_0|
=> |y_1| = |s_x*sin(a) s_y*cos(a)  0   t_y| * |y_0|
=> |z_1| = |0          0           s_z t_z| * |z_0|
=> |1  | = |0          0           0   1  | * |1  |

= | x_0 * s_x * cos(a) - y_0 * s_y * sin(a) + t_x |
= | x_0 * s_x * sin(a) + y_0 * s_y * cos(a) + t_y |
= | z_0 * s_z + t_z                               |
= | 1                                             |
```

### p<sub>2</sub> = M<sub>2</sub> * p<sub>0</sub>:
```
p_2 = M_2 * p_0

=> |x_2| = |s_x*cos(a) -s_x*sin(a) 0   t_x*s_x*cos(a)-t_y*s_x*sin(a)| * |x_0|
=> |y_2| = |s_y*sin(a) s_y*cos(a)  0   t_x*s_y*sin(a)+t_y*s_y*cos(a)| * |y_0|
=> |z_2| = |0          0           s_z t_z*s_z                      | * |z_0|
=> |1  | = |0          0           0   1                            | * |1  |

= | x_0 * s_x * cos(a) - y_0 * s_x * sin(a) + t_x * s_x * cos(a) - t_y * s_x * sin(a) |
= | x_0 * s_y * sin(a) + y_0 * s_y * cos(a) + t_x * s_y * sin(a) + t_y * s_y * cos(a) |
= | z_0 * s_z + t_z * s_z                                                             |
= | 1                                                                                 |

= | s_x(x_0 * cos(a) - y_0 * sin(a) + t_x * cos(a) - t_y * sin(a)) |
= | s_y(x_0 * sin(a) + y_0 * cos(a) + t_x * sin(a) + t_y * cos(a)) |
= | s_z(z_0 + t_z)                                                 |
= | 1                                                              |

= | s_x((x_0 + t_x)*cos(a) - (y_0 + t_y)*sin(a)) |
= | s_y((x_0 + t_x)*sin(a) + (y_0 + t_y)*cos(a)) |
= | s_z(z_0 + t_z)                               |
= | 1                                            |
```

## Inserting values
The same values used in `drawCubeA()` & `drawCubeB()` in `Main.java`
can also be used to illustrate just how different these points are.

This gives the following transformations:
- `S(s_x=3/4, s_y=3/4, s_z=3/4)`
- `T(t_x=5, t_y=-5, t_z=0)`
- `Rz(a=30°)`

With these, the point `p_0 = (1, 1, 1)` can be transformed

### Calculating p<sub>1</sub>
```
| x_0 * s_x * cos(a) - y_0 * s_y * sin(a) + t_x |
| x_0 * s_x * sin(a) + y_0 * s_y * cos(a) + t_y |
| z_0 * s_z + t_z                               |
| 1                                             |

= | 1 * 3/4 * cos(30°) - 1 * 3/4 * sin(30°) + 5 |
= | 1 * 3/4 * sin(30°) + 1 * 3/4 * cos(30°) - 5 |
= | 1 * 3/4 + 0                                 |
= | 1                                           |

= | 3(sqrt(3) - 1)/8 + 5 |
= | 3(1 + sqrt(3))/8 - 5 |
= | 3/4                  |
= | 1                    |

≈ | 5.27  |
≈ | -3.98 |
= | 0.75  |
= | 1     |

=> p_1 = (5.27, -3.98, 0.75)
```

### Calculating p<sub>2</sub>
```
| s_x((x_0 + t_x)*cos(a) - (y_0 + t_y)*sin(a)) |
| s_y((x_0 + t_x)*sin(a) + (y_0 + t_y)*cos(a)) |
| s_z(z_0 + t_z)                               |
| 1                                            |

= | 3/4((1 + 5)*cos(30°) - (1 - 5)*sin(30°)) |
= | 3/4((1 + 5)*sin(30°) + (1 - 5)*cos(30°)) |
= | 3/4(1 + 0)                               |
= | 1                                        |

= | 3(3sqrt(3) + 2)/4 |
= | 3(3 - 2sqrt(3))/4 |
= | 3/4               |
= | 1                 |

≈ | 5.4   |
≈ | -0.35 |
= | 0.75  |
= | 1     |

=> p_2 = (5.4, -0.35, 0.75)
```

## Conclusion
- M<sub>1</sub> transformation: p<sub>0</sub>(1, 1, 1) => p<sub>1</sub>(5.27, -3.98, 0.75)
- M<sub>2</sub> transformation: p<sub>0</sub>(1, 1, 1) => p<sub>2</sub>(5.4, -0.35, 0.75)

The resulting points from both transformations are not the same,
meaning the transformed point will be rendered in different locations on the screen,
depending on which transformation matrix is used.
Since both matrices are made of same transformations in different order,
**the order of transformations must therefore matter**.