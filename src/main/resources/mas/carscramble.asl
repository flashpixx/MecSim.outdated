// --- scrambling car agent -------------------------------------------------------------
// the distance of the predecessor car and the scrambling value are used to modify the
// current speed of the car, so the agent can flare lower to the predecessor car
// --------------------------------------------------------------------------------------


// scrambling value in [0,1] (0 low scrambling, 1 high scrambling)
ag_scramble(0.4).

// initial goal
!drive.

// acceleration
+!accelerate
   :  root_bind_mspeed(Speed) &
      root_bind_macceleration(Accelerate) &
      root_bind_mmaxspeed(MaxSpeed)
   <- .min([MaxSpeed, Speed+Accelerate], NewSpeed);
      mesim.set(self, m_speed, NewSpeed);
      !drive.

// deceleration
+!decelerate
   :  root_bind_mspeed(Speed) &
      root_bind_mdeceleration(Decelerate) &
      Decelerate > 0
   <- .max([5, Speed-Decelerate], NewSpeed);
      mecsim.set(self, m_speed, NewSpeed);
      !drive.

// if there is a predecessor, check braking distance
// braking distance will be calculated using gaussian sum
// @see https://de.wikipedia.org/wiki/Gau%C3%9Fsche_Summenformel
+!drive
    :    root_bind_mspeed(Speed) &
         root_bind_mdeceleration(Deceleration) &
    	 ag_predecessor([Predecessor]) &
	     not(.empty([Predecessor]))

    <-   // this beliefs will be updated
         mecsim.removeBelief(ag_predecessor(_));

	     // get distance to predecessing car
         Predecessor =.. [X|_];
         mecsim.literal2number(X,Distance);

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
