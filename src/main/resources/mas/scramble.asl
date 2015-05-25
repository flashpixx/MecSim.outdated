// scrambling value in [0,1] (0 low scrambling, 1 high scrambling)
ag_scramble(0.75).

// initial goal
!drive.




// deceleration (based on Nagel-Schreckenberg model)
+!decelerate
   :  m_speed(Speed) & m_deceleration(Decelerate) & Decelerate > 0
   <- .max([5, Speed-Decelerate], NewSpeed);
      set(self, m_speed, NewSpeed).


// acceleration (based on Nagel-Schreckenberg model)
+!accelerate
   :  m_speed(Speed) & m_acceleration(Accelerate) & m_maxspeed(MaxSpeed) & ag_scramble(Scramble)
   <- .min([MaxSpeed, Speed+Scramble*Accelerate], NewSpeed);
      set(self, m_speed, NewSpeed).


// catches the distance value of the precessor car
+ag_predecessor([Predecessor])
   :  true
   <- -ag_distance(_)[source(_)];
      .getFunctor(Predecessor, ag_distance);
      !drive.



// decelerate on check of scrambling value, speed and distance
+ag_distance(Distance)
   :  m_speed(Speed) & ag_scramble(Scramble) & Distance < Scramble * Speed
   <- !decelerate.


// accelerate on checkf of scamling value, speed and distance
+ag_distance(Distance)
   :  m_speed(Speed) & ag_scramble(Scramble) & Distance > Scramble * Speed
   <- !accelerate.




// default behaviour - accelerate
+!drive
   :  true
   <- !accelerate;
      !drive.
