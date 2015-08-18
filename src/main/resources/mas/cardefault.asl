// --- default car agent ----------------------------------------------------------------
// drives depend on the Nagel-Schreckenberg model, the deceleration and acceleration
// depends on the distance to the current predecessor car
// --------------------------------------------------------------------------------------


// acceleration
+!accelerate
   :  m_speed(Speed) &
      m_acceleration(Accelerate) &
      m_maxspeed(MaxSpeed)
   <- .min([MaxSpeed, Speed+Accelerate], NewSpeed);
      set(self, m_speed, NewSpeed);
      !drive.

// deceleration
+!decelerate
   :  m_speed(Speed) &
      m_deceleration(Decelerate) &
      Decelerate > 0
   <- .max([5, Speed-Decelerate], NewSpeed);
      set(self, m_speed, NewSpeed);
      !drive.

// driving call
+!drive
    :    m_speed(Speed) &
         m_deceleration(Deceleration) &
	     not (.empty([Predecessor]))

    <-   // get distance to predecessing car
         Predecessor =.. [X|_];
         mecsim.literal2number(X,Distance);

	     // add the speed range
         for ( .range(I, 1, math.ceil( Speed / 10 ) ) )
         {
             +speed_range(I * 10);
         }

	     // calculate braking distance with gaussian sum
         UpperSumIndex = math.floor( Speed / Deceleration );
         BrakingDistance = UpperSumIndex * ( Speed - 0.5 * Deceleration * ( UpperSumIndex + 1 ) );

	 	// check if predecessing car is too close
	 	if ( BrakingDistance > Scramble*Distance )
    	{
		    !decelerate;
    	}
    	else
    	{
		    !accelerate;
	    }.

// default behaviour - accelerate
+!drive
   :  true
   <- !accelerate.
