// --- default car agent ----------------------------------------------------------------
// drives depend on the Nagel-Schreckenberg model, the deceleration and acceleration
// depends on the distance to the current predecessor car
// --------------------------------------------------------------------------------------


// acceleration
+!accelerate
   :  root_bind_speed(Speed) &
      root_bind_acceleration(Accelerate) &
      root_bind_maxspeed(MaxSpeed)
   <- .min([MaxSpeed, Speed+Accelerate], NewSpeed);
      mecsim_propertyset(self, m_speed, NewSpeed);
      !drive.

// deceleration
+!decelerate
   :  root_bind_speed(Speed) &
      root_bind_deceleration(Decelerate) &
      Decelerate > 0
   <- .max([5, Speed-Decelerate], NewSpeed);
      mecsim_propertyset(self, m_speed, NewSpeed);
      !drive.

// driving call
+!drive
    :    root_bind_speed(Speed) &
         root_bind_deceleration(Deceleration) &
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
