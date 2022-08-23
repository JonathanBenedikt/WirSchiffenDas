import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MotorFinishedComponent } from './motor-finished.component';

describe('MotorFinishedComponent', () => {
  let component: MotorFinishedComponent;
  let fixture: ComponentFixture<MotorFinishedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MotorFinishedComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MotorFinishedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
