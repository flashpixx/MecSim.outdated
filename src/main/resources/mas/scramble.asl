// scrambling value (high = more scrambling)
ag_scramble(3).

// initial goal
!drive.




// deceleration (based on Nagel-Schreckenberg model)
+!decelerate
   :  m_speed(Speed) & m_deceleration(Decelerate) & Decelerate > 0
   <- .max([0, Speed-Decelerate], NewSpeed);
      set(self, m_speed, NewSpeed).


// acceleration (based on Nagel-Schreckenberg model)
+!accelerate
   :  m_speed(Speed) & m_acceleration(Accelerate) & m_maxspeed(MaxSpeed)
   <- .min([MaxSpeed, Speed+Accelerate], NewSpeed);
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
   <- -ag_position(_)[source(_)];
      -ag_predecessor([_])[source(_)];
      invoke(self, getCurrentPosition, [Triple, ag_position]);
      invoke(self, getPredecessor, [Map, ag_predecessor]);
      !accelerate;
      !drive.
