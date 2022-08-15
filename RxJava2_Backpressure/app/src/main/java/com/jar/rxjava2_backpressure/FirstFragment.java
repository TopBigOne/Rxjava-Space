package com.jar.rxjava2_backpressure;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.jar.rxjava2_backpressure.databinding.FragmentFirstBinding;

import io.reactivex.BackpressureStrategy;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private MainFlowableFunction mainFlowableFunction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainFlowableFunction = new MainFlowableFunction();

        initEvent();
    }

    private void initEvent() {
        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this).navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
        binding.buttonTestOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainFlowableFunction.startOne();
            }
        });

        binding.buttonTestTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainFlowableFunction.startTwo();
            }
        });


        binding.buttonTestThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainFlowableFunction.startThree();
            }
        });

        binding.buttonTestFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainFlowableFunction.startFour();
            }
        });

        binding.buttonTestFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainFlowableFunction.startFive();
            }
        });

        binding.buttonTestSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainFlowableFunction.startSix();
            }
        });

        binding.buttonTestSeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainFlowableFunction.startSeven();
            }
        });

        binding.buttonTestEight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainFlowableFunction.startEight();
            }
        });

        binding.buttonTestNineERROR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainFlowableFunction.startNine(BackpressureStrategy.ERROR);
            }
        });
        binding.buttonTestNineMISSING.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainFlowableFunction.startNine(BackpressureStrategy.MISSING);
            }
        });

        binding.buttonTestNineBUFFER.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainFlowableFunction.startNine(BackpressureStrategy.BUFFER);
            }
        });

        binding.buttonTestNineDROP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainFlowableFunction.startNine(BackpressureStrategy.DROP);
            }
        });

        binding.buttonTestNineLATEST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainFlowableFunction.startNine(BackpressureStrategy.LATEST);
            }
        });
        binding.buttonTestTen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainFlowableFunction.startTen(BackpressureMode.BUFFER);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}